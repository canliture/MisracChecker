package kr.ac.jbnu.ssel.misrac;

import kr.ac.jbnu.ssel.castparser.CDTParser;
import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.MiaraCRuleException;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;
import kr.ac.jbnu.ssel.misrac.ui.preference.MisraUIdataHandler;
import kr.ac.jbnu.ssel.misrac.ui.preference.Rule;
import kr.ac.jbnu.ssel.misrac.util.Files;
import lombok.extern.log4j.Log4j2;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;
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

    private static final String RESULT_NAME_PREFIX = "misra_c_result";

    private static final String[] C_SUFFIX = new String[] { ".c", ".C", ".h" };

    String srcPath;
    String resultPath;

    private List<AbstractMisraCRule> checkers;

    private List<IASTTranslationUnit> astList;

    private Set<String> sourceFiles = new TreeSet<>();

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
        checker.parse();

        List<ViolationMessage> msgList = new ArrayList<>();
        List<IASTTranslationUnit> trans = checker.astList;
        for (IASTTranslationUnit unit : trans) {
            msgList.addAll(checker.checkTranslation(unit));
        }

        // internal implementation, not open source.
        ReportWriter writer = new ReportWriter(checker, msgList);
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

    private List<ViolationMessage> checkTranslation(IASTTranslationUnit unit) {
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

        List<ViolationMessage> msgList = new ArrayList<>();
        for (AbstractMisraCRule checker : checkers) {
            try {
                checker.checkRule();
                if (checker.isViolated()) {
                    ViolationMessage[] msgs = checker.getViolationMessages();
                    msgList.addAll(Arrays.asList(msgs));
                }
            } catch (MiaraCRuleException e) {
                e.printStackTrace();
            }
        }

        return msgList;
    }
}
