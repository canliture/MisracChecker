package kr.ac.jbnu.ssel.misrac.rulesupport;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import java.util.Objects;

/**
 * @author "STKIM"
 */
public class ViolationMessage {
	private AbstractMisraCRule rule;

	private String message;
	private IASTNode node;
	private String cFilePath;

	private ViolationLevel violationLevel;

	public ViolationMessage(AbstractMisraCRule rule, String message, IASTNode node, ViolationLevel violationLevel) {
		this.rule = rule;
		this.message = message;
		this.node = node;
		this.violationLevel = violationLevel;
	}

	public ViolationMessage(AbstractMisraCRule rule, String message, IASTNode node) {
		this(rule,message,node,ViolationLevel.severe);
	}

	public AbstractMisraCRule getRule() {
		return rule;
	}

	public String getMessage() {
		return message;
	}

	public IASTNode getNode() {
		return node;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ViolationLevel getViolationLevel() {
		return violationLevel;
	}

	public void setViolationLevel(ViolationLevel violationLevel) {
		this.violationLevel = violationLevel;
	}

	public String getCFilePath() {
		return cFilePath;
	}

	public void setCFilePath(String classFilePath) {
		this.cFilePath = classFilePath;
	}

	@Override
	public String toString() {
		return rule.getRuleID() + " - violation - " + message + ", node:" + node.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(rule, node, cFilePath);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ViolationMessage that = (ViolationMessage) o;
		return Objects.equals(rule, that.rule) && Objects.equals(node, that.node) && Objects.equals(cFilePath, that.cFilePath);
	}
}
