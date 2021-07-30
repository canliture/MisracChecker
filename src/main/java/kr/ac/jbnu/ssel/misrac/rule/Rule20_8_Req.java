package kr.ac.jbnu.ssel.misrac.rule;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.MessageFactory;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;


/**
 * The signal handling facilities of <signal.h> shall not be used.
 *
 * Signal handling contains implementation-defined and undefined behaviour.
 *
 * [STATUS: DONE]
 * @author stkim
 *
 */

public class Rule20_8_Req extends AbstractMisraCRule{
	
	 private final static String Signal_H = "#include <signal.h>";
	   
	   public Rule20_8_Req(IASTTranslationUnit ast) {
	      super("Rule20_8_Req", false, ast);
	      shouldVisitPreprocessor = true;
	   }

	   @Override
	   protected int visit(IASTPreprocessorIncludeStatement includeStatement) {

	      if(includeStatement.getRawSignature().equals(Signal_H)){
	    	  
//	    	  The signal handling facilities of <signal.h> shall not be used.
	         String message1 = MessageFactory.getInstance().getMessage(5123);
	         violationMsgs.add(
	               new ViolationMessage(this, getRuleID() + ":" + message1 + "--" + Signal_H, includeStatement));
	         
	         isViolated = true;
	      }
	      return super.visit(includeStatement);
	   }
}
