package kr.ac.jbnu.ssel.misrac.rule;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.MessageFactory;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;

public class Rule02_3_Req extends AbstractMisraCRule{
	
	  private static final String COMMENT_Start = "/*";
	    private static final String COMMENT_End = "*/";

	    public Rule02_3_Req(IASTTranslationUnit ast) {
		super("Rule02_3_Req", false, ast);
		shouldVisitComment = true;
	    }

	 @Override
	    protected int visit(IASTComment comment) {
		 System.out.println(comment.getComment());

		if (comment.toString().startsWith(COMMENT_Start)&&!(comment.toString().endsWith(COMMENT_End))) {
		    
		    String msg = MessageFactory.getInstance().getMessage(3108);

		    if (violationMsgs == null) {
			violationMsgs = new ArrayList<ViolationMessage>();
		    }

		    violationMsgs.add(new ViolationMessage(this, getRuleID() + ":"+ msg + "--" + comment.toString(), comment));
		    isViolated = true;
		}
		return super.visit(comment);
	    }

}
