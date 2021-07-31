package kr.ac.jbnu.ssel.misrac;

import javafx.util.Pair;
import kr.ac.jbnu.ssel.castparser.CDTParser;
import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.MiaraCRuleException;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;
import kr.ac.jbnu.ssel.misrac.util.Files;
import lombok.extern.log4j.Log4j2;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.reflections.Reflections;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by Liture on 2021/7/28
 */
@Log4j2
public class MISRACChecker {

    private static final String HELP = "\n"
                                     + "-src {被扫描源码目录} \n"
                                     + "-rst {输出结果文件路径}\n";

    private static final String[] C_SUFFIX = new String[] { ".c", ".C", ".h" };

    String srcPath;
    String resultPath;

    private List<AbstractMisraCRule> checkers;

    private List<IASTTranslationUnit> astList;

    private Set<String> sourceFiles = new TreeSet<>();

    private int fileCounts;

    private Map<String, Pair<Integer, Integer>> suffix2CountAndLines = new HashMap<>();

    private void calStatics() {
        fileCounts = sourceFiles.size();
        for (String sourceFile : sourceFiles) {
            String suffix = sourceFile.substring(sourceFile.lastIndexOf('.')+1);
            Pair<Integer, Integer> countAndLines = suffix2CountAndLines.get(suffix);
            if (countAndLines == null) {
                countAndLines = new Pair<>(0, 0);
            }
            int count = countAndLines.getKey() + 1;
            int lines = countAndLines.getValue() + Files.lineCounts(sourceFile);
            suffix2CountAndLines.put(suffix, new Pair<>(count, lines));
        }
    }

    public int getFileCounts() {
        return fileCounts;
    }

    public Map<String, Pair<Integer, Integer>> getSuffix2CountAndLines() {
        return suffix2CountAndLines;
    }

    /**
     * -src 被扫描源码目录, 必选
     * -rst 输出结果文件路径, 可选
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(HELP);
        }

        MISRACChecker checker = new MISRACChecker();
        checker.processCommandLine(args);
        Files.listFilesWithExtends(checker.srcPath, C_SUFFIX, checker.sourceFiles);
        checker.calStatics();
        checker.parse();

        Set<ViolationMessage> msgSet = new HashSet<>();
        List<IASTTranslationUnit> trans = checker.astList;
        for (IASTTranslationUnit unit : trans) {
            msgSet.addAll(checker.checkTranslation(unit));
        }

        // internal implementation, not open source.
        ReportWriter writer = new ReportWriter(checker, new ArrayList<>(msgSet));
        writer.report();
    }

    private void processCommandLine(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-src":
                    srcPath = args[++i];
                    break;
                case "-rst":
                    resultPath = args[++i];
                    break;
                default:
                    break;
            }
        }
        if (srcPath == null) {
            throw new IllegalArgumentException(HELP);
        }
        try {
            srcPath = new File(srcPath).getCanonicalPath();
            if (resultPath == null) {
                resultPath = srcPath;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(HELP);
        }
    }

    private void parse() {
        if (astList != null) {
            return;
        }
        astList = new ArrayList<>();
        for (String sourceFile : sourceFiles) {
            IASTTranslationUnit ast = null;
            try {
                ast = CDTParser.createAST(sourceFile);
            } catch (Exception e) {
                log.error("Can't create AST from file {}", sourceFile);
                continue;
            }
            astList.add(ast);
        }
    }

    private Set<ViolationMessage> checkTranslation(IASTTranslationUnit unit) {
        checkers = new ArrayList<>();
        String prefix = "kr.ac.jbnu.ssel.misrac.rule.";
        Reflections f = new Reflections(prefix);
        Set<Class<?>> classes = null;
        try {
            classes = f.getSubTypesOf((Class<Object>) Class.forName("kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule"));
        } catch (ClassNotFoundException e) {
            log.error("Can't getSubTypesOf  kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule");
        }
        Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class<?> clazz = iterator.next();
            String name = clazz.getName();
            if (!name.startsWith(prefix)) {
                iterator.remove();
            }
        }
        for (Class<?> aClass : classes) {
            Constructor constructor = null;
            try {
                constructor = aClass.getConstructor(IASTTranslationUnit.class);
            } catch (NoSuchMethodException e) {
                log.error("Can't load IASTTranslationUnit's constructor");
                continue;
            }
            AbstractMisraCRule checker = null;
            try {
                checker = (AbstractMisraCRule) constructor.newInstance(unit);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("Can't instantiate the class by constructor(IASTTranslationUnit): {}", aClass.getName());
            }
            checkers.add(checker);
        }

        Set<ViolationMessage> msgSet = new HashSet<>();
        for (AbstractMisraCRule checker : checkers) {
            try {
                checker.checkRule();
                if (checker.isViolated()) {
                    ViolationMessage[] msgs = checker.getViolationMessages();
                    msgSet.addAll(Arrays.asList(msgs));
                }
            } catch (MiaraCRuleException e) {
                e.printStackTrace();
            }
        }

        return msgSet;
    }
}
