package kr.ac.jbnu.ssel.misrac.rule;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import kr.ac.jbnu.ssel.misrac.rulesupport.AbstractMisraCRule;
import kr.ac.jbnu.ssel.misrac.rulesupport.MessageFactory;
import kr.ac.jbnu.ssel.misrac.rulesupport.ViolationMessage;

/**
 * MISRA-C:2004 Rule 13.05: (Required) The three expressions of a for statement
 * shall be concerned only with loop control.
 * 
 * The three expressions of a for statement shall be used only for these
 * purposes: First expression - Initialising the loop counter
 * 
 * Second expression - Shall include testing the loop counter, and optionally
 * other loop control variables
 * 
 * Third expression - Increment or decrement of the loop counter
 * 
 * The following options are allowed:
 * 
 * All three expressions shall be present; The second and third expressions
 * shall be present with prior initialisation of the loop counter; All three
 * expressions shall be empty for a deliberate infinite loop.
 * 
 * [STATUS: DONE]
 * 
 *  
 * @author Seunghyeon Kang
 */

public class Rule13_5_Req extends AbstractMisraCRule {

	public Rule13_5_Req(IASTTranslationUnit ast) {
		super("Rule13_5_Req", false, ast);

		shouldVisitStatements = true;
		shouldVisitExpressions = true;
	}

	public Rule13_5_Req(String ruleID, boolean visitNodes, IASTTranslationUnit ast) {
		super(ruleID, visitNodes, ast);
	}

	@Override
	protected int visit(IASTForStatement forStatement) {
		String firstExpVar ="";
		org.eclipse.cdt.core.dom.ast.IASTNode[] forChild = forStatement.getChildren();

		for (IASTNode iastNode : forChild) {

			if( iastNode instanceof IASTExpressionStatement)
			{
				firstExpVar = ((IASTExpressionStatement)iastNode).getExpression().getRawSignature();
				IASTExpressionStatement shouldBeAssignExp = (IASTExpressionStatement)iastNode;
				if( !( shouldBeAssignExp.getExpression().getRawSignature().equals(IASTBinaryExpression.op_assign)) )
				{
					String msg = MessageFactory.getInstance().getMessage(2462);
					violationMsgs.add(new ViolationMessage(this, getRuleID() + ":" + msg + ":" + iastNode.getRawSignature(),
							iastNode));
					isViolated = true;
				}
			}
			else if(iastNode instanceof IASTBinaryExpression){
				String secondExp = ((IASTBinaryExpression)iastNode).getOperand1().toString();
				if(!secondExp.equals(firstExpVar)){
					String msg = "The second expressions shall be present with prior initialisation of the loop counter;";
					violationMsgs.add(new ViolationMessage(this, getRuleID() + ":" + msg + ":" + iastNode.getRawSignature(),
							iastNode));
					isViolated = true;
				}
			}
			else if(iastNode instanceof IASTUnaryExpression){
				String thirdExp = ((IASTUnaryExpression) iastNode).getOperand().toString();
				if(!thirdExp.equals(firstExpVar)){
					String msg = MessageFactory.getInstance().getMessage(2463);
					violationMsgs.add(new ViolationMessage(this, getRuleID() + ":" + msg + ":" + iastNode.getRawSignature(),
							iastNode));
					isViolated = true;
				}
			}
		}

		return super.visit(forStatement);
	}

}