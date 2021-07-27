package test.kr.ac.jbnu.ssel.misrac.rule.testsupport;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;
import org.reflections.Reflections;
import test.kr.ac.jbnu.ssel.misrac.rule.testC.CCode;

public class TestResourceMgr
{
	public static IASTTranslationUnit getTranslationUnit(String cfileName) throws Exception
	{
		String path = CCode.class.getPackage().getName().replace('.', '/') + "/" + cfileName;

		ClassLoader loader = CCode.class.getClassLoader();
		URL pathURL = loader.getResource(path);
		File file = Paths.get(pathURL.toURI()).toFile();
		FileContent fc = FileContent.createForExternalFileLocation(file.getPath());
		IASTTranslationUnit ast = creatIASTTranslationUnit(fc);
		return ast;
	}

	public static IASTTranslationUnit creatIASTTranslationUnit(FileContent input) throws Exception {
		Map<String, String> macroDefinitions = new HashMap<String, String>();
		IScannerInfo si = new ScannerInfo(macroDefinitions, new String[]{TestEnv.MINGW_INCLUDE_PATH});
		IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
		IIndex idx = null;
		int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		IParserLogService log = new DefaultLogService();
		return GPPLanguage.getDefault().getASTTranslationUnit(input, si, ifcp, idx, options, log);
	}

	public static void main(String[] args) throws Exception
	{
		IASTTranslationUnit astT = TestResourceMgr.getTranslationUnit("TestRule08_1.C");

		ArrayList<ViolationMessage> violationMessages = new ArrayList<ViolationMessage>();

		String prefix = "kr.ac.jbnu.ssel.misrac.rule.";
		Reflections f = new Reflections(prefix);
		Set<Class<?>> classes = f.getSubTypesOf((Class<Object>) Class.forName("kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule"));
		Iterator<Class<?>> iterator = classes.iterator();
		while (iterator.hasNext()) {
			Class<?> clazz = iterator.next();
			String name = clazz.getName();
			if (!name.startsWith(prefix)) {
				iterator.remove();
			}
		}
		List<AbstractMisraCRule> checkers = new ArrayList<>();
		for (Class<?> aClass : classes) {
			Constructor constructor = aClass.getConstructor(IASTTranslationUnit.class);
			AbstractMisraCRule checker = (AbstractMisraCRule) constructor.newInstance(astT);
			checkers.add(checker);
		}

		for (AbstractMisraCRule checker : checkers) {
			checker.checkRule();
			if(checker.isViolated())
			{
				ViolationMessage[] violationMsgs = checker.getViolationMessages();
				violationMessages.addAll(Arrays.asList(violationMsgs));
			}
		}

		for (ViolationMessage violationMessage : violationMessages) {
			System.out.println(violationMessage);
		}
	}
}
