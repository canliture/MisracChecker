package kr.ac.jbnu.ssel.castparser;

import javafx.util.Pair;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Liture on 2021/7/28
 */
public class CDTParser {

    public static IASTTranslationUnit createAST(String filePath) throws Exception {
        FileContent fileContent = FileContent.createForExternalFileLocation(filePath);
        return createAST(fileContent);
    }

    public static IASTTranslationUnit createAST(FileContent input) throws Exception {
        Map<String, String> macroDefinitions = new HashMap<>();
        IScannerInfo si = new ScannerInfo(macroDefinitions, new String[0]);
        IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex idx = null;
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();
        return GPPLanguage.getDefault().getASTTranslationUnit(input, si, ifcp, idx, options, log);
    }

    public static Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getRangeInSource(IASTFileLocation fileLocation) {
        Class<?> cls = fileLocation.getClass();
        try {
            Field field = cls.getDeclaredField("fLocationCtx");
            field.setAccessible(true);
            Object obj = field.get(fileLocation);
            Method method = obj.getClass().getSuperclass().getDeclaredMethod("computeLineOffsets");
            method.setAccessible(true);
            int[] arrays = (int[]) method.invoke(obj);
            int line = fileLocation.getStartingLineNumber();
            int lineEnd = fileLocation.getEndingLineNumber();
            int column = getColumn(arrays,line, fileLocation.getNodeOffset())-1;
            int columnEnd = getColumn(arrays,lineEnd, fileLocation.getNodeOffset()+ fileLocation.getNodeLength())-1;
            return new Pair<>(new Pair<>(line, lineEnd), new Pair<>(column, columnEnd));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int getColumn(int[] arrays, int line, int length){
        if(length == 0){
            return 0;
        }
        if(arrays.length == 0 || arrays[0] > length){
            return length;
        }
        for (int i = 0; i < arrays.length - 1; i++) {
            if(arrays[i] == length){
                if(i == 0){
                    return length;
                }else {
                    return length - arrays[i-1];
                }
            }
            if(arrays[i] <= length && arrays[i+1] > length){
                return length - arrays[i];
            }
        }
        return length - arrays[arrays.length - 1];
    }

    public static String getRelativePath(IASTFileLocation loc, String dir) {
        String path = loc.getFileName();
        String result = path.substring(dir.length() + 1);
        result = result.replaceAll("\\\\", "/");
        return result;
    }
}
