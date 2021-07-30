package kr.ac.jbnu.ssel.misrac.ui.preference;

import kr.ac.jbnu.ssel.misrac.rule.RuleLocation;
import kr.ac.jbnu.ssel.misrac.ui.Constant;
import org.eclipse.core.runtime.FileLocator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * 
 * @author Taeyoung Kim
 */
public class MisraUIdataHandler implements Cloneable {

	private static MisraUIdataHandler instance;

	private List<Rule> ruleList;
	private HashMap<String, List<Rule>> rulesByCategory = new HashMap<String, List<Rule>>();

	private MisraUIdataHandler() {
		try {
			loadAllRules();
			Collections.sort(ruleList, new java.util.Comparator<Rule>() {
				@Override
				public int compare(Rule rule1, Rule rule2) {
					double rule1MinerNum = Double.parseDouble(rule1.minerNum);
					double rule2MinerNum = Double.parseDouble(rule2.minerNum);
					return Double.compare(rule1MinerNum, rule2MinerNum);
				}
			});
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static MisraUIdataHandler getInstance() {
		if (instance == null) {
			instance = new MisraUIdataHandler();
		}

		return instance;
	}

	public HashSet<Rule> getShouldCheckRules() {
		HashSet<Rule> shouldCheckRules = new HashSet<Rule>();
		for (Rule rule : ruleList) {
			if (rule.isShouldCheck()) {
				shouldCheckRules.add(rule);
			}
		}
		return shouldCheckRules;
	}

	public List<Rule> getRules() {
		return ruleList;
	}

	public List<Rule> getRules(String category) throws JAXBException {
		return rulesByCategory.get(category);
	}

	public List<Rule> loadAllRules() throws JAXBException {
		if (ruleList != null) {
			return ruleList;
		}
		URL url = getClass().getClassLoader().getResource("rule_description.xml");
		String filePath = url.getPath();
		File file = new File(filePath);
		JAXBContext jc = JAXBContext.newInstance(Rules.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Rules rules = (Rules) unmarshaller.unmarshal(file);
		ruleList = rules.getRule();

		for (Rule rule : ruleList) {
			if (rule.getCategory() != null) {
				String category = rule.getCategory();
				List<Rule> ruleCate = rulesByCategory.computeIfAbsent(category, k -> new ArrayList<>());

				ruleCate.add(rule);
			}
		}

		return ruleList;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void storeToXml() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(String.class, Rules.class);
			Marshaller marshaller = context.createMarshaller();
			URL url = getClass().getClassLoader().getResource("rule_description.xml");
			String filePath = url.getPath();
			File rulesFile = new File(filePath);
			Rules rules = new Rules();
			setClassNames();
			rules.setRule(ruleList);
			marshaller.marshal(rules, rulesFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	
	private void setClassNames() {
		for (Rule rule : ruleList) {
			if(rule.className.contains(Constant.notImplement)){
				try {
					String className = getClassName(rule.minerNum);
					if(className==null){
						continue;
					}else{
						rule.setClassName(className);
					}
				} catch (URISyntaxException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private String getClassName(String minerNum) throws URISyntaxException, IOException {
		String className = null;
		ClassLoader loader = RuleLocation.class.getClassLoader();
		URL ruleCodeClassDictory = loader.getResource( RuleLocation.class.getPackage().getName().replace('.', '/'));
		URL fileURL = FileLocator.toFileURL(ruleCodeClassDictory);

		File ruleDicFile = new File(fileURL.toURI());

		String[] ruleCodeFiles = ruleDicFile.list();
		for (String ruleCodeClass : ruleCodeFiles) {
			if(!ruleCodeClass.equals("RuleLocation.class"))
			{
				String classRuleNumber = getClassRuleNumber(ruleCodeClass);
				if(minerNum.equals(classRuleNumber))
				{
					className = ruleCodeClass;
					break;
				}
			}
		}
		return className;
	}

	private String getClassRuleNumber(String ruleCodeClass) {
		StringBuilder wholeRuleNumber = new StringBuilder();
	 	String ruleNum = ruleCodeClass.substring(4);
	 	String[] removedClass = ruleNum.split(".class");
	 	String[] subStrings = removedClass[0].split("_");
	 	String startOfNum= null;
	 	if(subStrings[0].startsWith("0")){
	 		startOfNum = subStrings[0].replace("0", "");
	 	}else{
	 		startOfNum = subStrings[0];
	 	}
		String endOfNum = subStrings[1];
		wholeRuleNumber.append(startOfNum);
		wholeRuleNumber.append(".").append(endOfNum);
		return wholeRuleNumber.toString();
	}
	
	public static void main(String[] args) throws JAXBException {
		MisraUIdataHandler misraUIdataHandler = new MisraUIdataHandler();
		List<Rule> ruleList = misraUIdataHandler.loadAllRules();
		for (Rule rule : ruleList) {
			System.out.println(rule.getMinerNum());
		}
	}

}
