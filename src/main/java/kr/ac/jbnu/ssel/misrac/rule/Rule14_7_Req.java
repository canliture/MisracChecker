package kr.ac.jbnu.ssel.misrac.rule;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.MessageFactory;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;

/**
 * A function shall have a single point of exit at the end of the function.
 *
 * This is required by IEC 61508, under good programming style.
 * 
 *[STATUS: DONE]
 *
 * @author sangjin
 *
 */
public class Rule14_7_Req extends AbstractMisraCRule {

	private static int count = 0;

	public Rule14_7_Req(IASTTranslationUnit ast) {
		super("Rule14_7_Req", false, ast);
		shouldVisitDeclarations = true;
		shouldVisitDeclarators = true;
		shouldVisitComment = false;
	}

	@Override
	protected int visit(IASTFunctionDefinition functionDefinition) {

		for (IASTNode child : functionDefinition.getChildren()) {
			if (child instanceof IASTCompoundStatement) {
				for (IASTNode grandChild : child.getChildren()) {
					if (grandChild instanceof IASTIfStatement) {
						for (IASTNode temp : grandChild.getChildren()) {
							if (temp instanceof IASTCompoundStatement) {
								if (temp.getChildren()[0] instanceof IASTReturnStatement) {
									count++;
									if (count > 1) {
										
//										This function has more than one 'return' path.
										String message1 = MessageFactory.getInstance().getMessage(2889);
										violationMsgs.add(new ViolationMessage(this,
												getRuleID() + ":" + message1 + "--" + temp.getChildren()[0].getRawSignature(), temp.getChildren()[0]));
										isViolated = true;
									}
								}
							}
						}
					}
				}
			}
		}

		
		return super.visit(functionDefinition);
	}
}
