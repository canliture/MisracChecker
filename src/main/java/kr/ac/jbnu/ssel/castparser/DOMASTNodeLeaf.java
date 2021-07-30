/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package kr.ac.jbnu.ssel.castparser;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTProblemHolder;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.c.ICASTArrayDesignator;
import org.eclipse.cdt.core.dom.ast.c.ICASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.c.ICASTFieldDesignator;
import org.eclipse.cdt.core.dom.ast.gnu.c.IGCCASTArrayRangeDesignator;
import org.eclipse.cdt.core.parser.Keywords;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;
import org.eclipse.core.runtime.IAdaptable;

/**
 * @author dsteffle
 */
public class DOMASTNodeLeaf implements IAdaptable {
	private static final String INTERNAL = "internal"; //$NON-NLS-1$
	private static final String VARIABLE_SIZED_ = "* "; //$NON-NLS-1$
	private static final String VOLATILE_ = "volatile "; //$NON-NLS-1$
	private static final String STATIC_ = "static "; //$NON-NLS-1$
	private static final String RESTRICT_ = "restrict "; //$NON-NLS-1$
	private static final String CONST_ = "const "; //$NON-NLS-1$
	private static final String DASH = "-"; //$NON-NLS-1$
	private static final String FILE_SEPARATOR = "\\"; //$NON-NLS-1$
	public static final String BLANK_STRING = ""; //$NON-NLS-1$
	private static final String IGCCAST_PREFIX = "IGCCAST"; //$NON-NLS-1$
	private static final String IGNUAST_PREFIX = "IGNUAST"; //$NON-NLS-1$
	private static final String IGPPAST_PREFIX = "IGPPAST"; //$NON-NLS-1$
	private static final String ICPPAST_PREFIX = "ICPPAST"; //$NON-NLS-1$
	private static final String ICAST_PREFIX = "ICAST"; //$NON-NLS-1$
	private static final String IAST_PREFIX = "IAST"; //$NON-NLS-1$
	private static final String START_OF_LIST = ": "; //$NON-NLS-1$
	private static final String LIST_SEPARATOR = ", "; //$NON-NLS-1$
	private static final String PERIOD = "."; //$NON-NLS-1$
	private IASTNode node = null;
	private DOMASTNodeParent parent;

	// used for applying filters to the tree, since it is lazily populated
	// all parents of the desired tree object to display need to have a flag as well
	private int filterFlag = 0;
	private static Set<String> ignoreInterfaces= new HashSet<String>();
	public static final int FLAG_PROBLEM = 1<<0;
	public static final int FLAG_PREPROCESSOR = 1<<1;
	public static final int FLAG_INCLUDE_STATEMENTS = 1<<2;
	static {
		ignoreInterfaces.addAll(Arrays.asList(new String[] {
				"IASTCompletionContext", "ICPPASTCompletionContext", "IASTNode"
		}));
	}
	public DOMASTNodeLeaf(IASTNode node) {
		this.node = node;
	}
	public IASTNode getNode() {
		return node;
	}
	public void setParent(DOMASTNodeParent parent) {
		this.parent = parent;
	}
	public DOMASTNodeParent getParent() {
		return parent;
	}

	private boolean hasProperPrefix(String string) {
		if (string.startsWith(IAST_PREFIX) ||
				string.startsWith(ICAST_PREFIX) ||
				string.startsWith(ICPPAST_PREFIX) ||
				string.startsWith(IGPPAST_PREFIX) ||
				string.startsWith(IGNUAST_PREFIX) ||
				string.startsWith(IGCCAST_PREFIX)) {
			if (!ignoreInterfaces.contains(string)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		if( node == null ) return BLANK_STRING;
		StringBuffer buffer = new StringBuffer();
		List<Class<?>> search= new LinkedList<Class<?>>();
		boolean done= false;
		boolean needComma= false;

		for (search.add(node.getClass()); !search.isEmpty(); ) {
			Class<?> clazz= search.remove(0);
			if (clazz.isInterface()) {
				if (clazz.getPackage().toString().indexOf(INTERNAL) < 0) {
					String interfaceName= clazz.getName();
					interfaceName= interfaceName.substring(interfaceName.lastIndexOf(PERIOD) + 1);
					if (hasProperPrefix(interfaceName)) {
						if (needComma)
							buffer.append(LIST_SEPARATOR);
						buffer.append(interfaceName);
						needComma= true;
						done= true;
					}
				}
			}
			if (!done) {
				search.addAll(Arrays.asList((Class<?>[])clazz.getInterfaces()));
				final Class<?> superclass = clazz.getSuperclass();
				if (superclass != null)
					search.add(superclass);
			}
		}

		if ( node instanceof IASTProblemHolder ) {
			buffer.append(START_OF_LIST);
			buffer.append(((IASTProblemHolder)node).getProblem().getMessageWithLocation());
		} else if ( node instanceof IASTSimpleDeclaration ) {
			String name = null;
			IASTDeclarator[] decltors = ((IASTSimpleDeclaration)node).getDeclarators();

			if ( decltors.length > 0 ) {
				buffer.append(START_OF_LIST);
				for (int i=0; i<decltors.length; i++) {
					name = getDeclaratorName(decltors[i]);
					buffer.append(name);

					if (i+1<decltors.length)
						buffer.append(LIST_SEPARATOR);
				}
			}
			return buffer.toString();
		} else if ( node instanceof IASTFunctionDefinition ) {
			String name = getDeclaratorName( ((IASTFunctionDefinition)node).getDeclarator() );
			if (name != null) {
				buffer.append(START_OF_LIST);
				buffer.append(name);
			}
			return buffer.toString();
		} else if ( node instanceof IASTName ) {
			buffer.append(START_OF_LIST);
			buffer.append(node);
			return buffer.toString();
		} else if ( node instanceof IASTTranslationUnit ) {
			String fileName = ((IASTTranslationUnit)node).getFilePath();
			int lastSlash = fileName.lastIndexOf(FILE_SEPARATOR);

			if (lastSlash > 0) {
				buffer.append(START_OF_LIST);
				buffer.append(fileName.substring(lastSlash+1)); // TODO make path relative to project, i.e. /projectName/path/file.c
			}

			return buffer.toString();
		} else if( node instanceof IASTDeclSpecifier )
		{
			buffer.append( START_OF_LIST );
			buffer.append( getSignature((IASTDeclSpecifier)node) );
			return buffer.toString();
		} else if ( node instanceof IASTPreprocessorIncludeStatement ) {
			String path = ((IASTPreprocessorIncludeStatement)node).getPath();
			int lastSlash = path.lastIndexOf(FILE_SEPARATOR) + 1;
			buffer.append( START_OF_LIST );
			buffer.append( path.substring(lastSlash) );
		} else if ( node instanceof IASTPreprocessorObjectStyleMacroDefinition ) {
			String name = ((IASTPreprocessorObjectStyleMacroDefinition)node).getName().toString();
			if (name != null) {
				buffer.append( START_OF_LIST );
				buffer.append( name );
			}
		} else if ( node instanceof IASTLiteralExpression ) {
			buffer.append(START_OF_LIST);
			buffer.append(node.toString());
		} else if ( node instanceof IASTCastExpression ) {
			buffer.append(START_OF_LIST);
			buffer.append( ASTStringUtil.getCastOperatorString( (IASTCastExpression)node ) );
		} else if ( node instanceof IASTUnaryExpression ) {
			buffer.append(START_OF_LIST);
			buffer.append( ASTStringUtil.getUnaryOperatorString( (IASTUnaryExpression)node ) );
		} else if ( node instanceof IASTBinaryExpression ) {
			buffer.append(START_OF_LIST);
			buffer.append( ASTStringUtil.getBinaryOperatorString( (IASTBinaryExpression)node ) );
		} else if ( node instanceof ICASTDesignator ) {
			if ( node instanceof ICASTArrayDesignator && ((ICASTArrayDesignator)node).getSubscriptExpression() != null ) {
				buffer.append(START_OF_LIST);
				buffer.append(((ICASTArrayDesignator)node).getSubscriptExpression());
			} else if ( node instanceof ICASTFieldDesignator && ((ICASTFieldDesignator)node).getName() != null ) {
				buffer.append(START_OF_LIST);
				buffer.append(((ICASTFieldDesignator)node).getName());
			} else if ( node instanceof IGCCASTArrayRangeDesignator && ((IGCCASTArrayRangeDesignator)node).getRangeCeiling() != null && ((IGCCASTArrayRangeDesignator)node).getRangeFloor() != null ) {
				buffer.append(START_OF_LIST);
				buffer.append(((IGCCASTArrayRangeDesignator)node).getRangeCeiling());
				buffer.append(DASH);
				buffer.append(((IGCCASTArrayRangeDesignator)node).getRangeFloor());
			}
		} else if ( node instanceof IASTArrayModifier ) {
			boolean started = false;
			if ( node instanceof ICASTArrayModifier ) {
				started = true;
				buffer.append(START_OF_LIST);
				if (((ICASTArrayModifier)node).isConst()) buffer.append(CONST_);
				if (((ICASTArrayModifier)node).isRestrict()) buffer.append(RESTRICT_);
				if (((ICASTArrayModifier)node).isStatic()) buffer.append(STATIC_);
				if (((ICASTArrayModifier)node).isVolatile()) buffer.append(VOLATILE_);
				if (((ICASTArrayModifier)node).isVariableSized()) buffer.append(VARIABLE_SIZED_);
			}

			IASTExpression constantExpression = ((IASTArrayModifier)node).getConstantExpression();
			if ( constantExpression != null && constantExpression instanceof IASTIdExpression ) {
				if (!started) buffer.append(START_OF_LIST);
				buffer.append(((IASTIdExpression)constantExpression).getName().toString());
			}
		} else if ( node instanceof IASTPointer ) {
			boolean started = false;

			if (((IASTPointer)node).isConst()) {
				if (!started) {
					started = true;
					buffer.append(START_OF_LIST);
				}
				buffer.append(CONST_);
			}

			if (((IASTPointer)node).isVolatile()) {
				if (!started) {
					started = true;
					buffer.append(START_OF_LIST);
				}
				buffer.append(VOLATILE_);
			}

			if (((IASTPointer)node).isRestrict()) {
				if (!started) {
					started = true;
					buffer.append(START_OF_LIST);
				}
				buffer.append(RESTRICT_);
			}

		}

		return buffer.toString();
	}
	private String getSignature(IASTDeclSpecifier declSpec) {
		if (declSpec instanceof IASTCompositeTypeSpecifier) {
			StringBuilder buf= new StringBuilder();
			IASTCompositeTypeSpecifier comp = (IASTCompositeTypeSpecifier) declSpec;
			switch(comp.getKey()) {
				case IASTCompositeTypeSpecifier.k_struct:
					buf.append(Keywords.cSTRUCT);
					break;
				case IASTCompositeTypeSpecifier.k_union:
					buf.append(Keywords.cUNION);
					break;
				default:
					buf.append(Keywords.cCLASS);
					break;
			}
			buf.append(' ');
			buf.append(comp.getName().toString());
			return buf.toString();
		} else if (declSpec instanceof IASTEnumerationSpecifier) {
			StringBuilder buf= new StringBuilder();
			IASTEnumerationSpecifier comp = (IASTEnumerationSpecifier) declSpec;
			buf.append(Keywords.cENUM);
			buf.append(' ');
			buf.append(comp.getName().toString());
			return buf.toString();
		}
		String intermed= declSpec.getRawSignature();
		return intermed.replaceAll("\\s+", " ");
	}

	private String getDeclaratorName(IASTDeclarator decltor) {
		String name = BLANK_STRING;
		while (decltor != null && decltor.getName() != null && decltor.getName().toString() == null) {
			decltor = decltor.getNestedDeclarator();
		}
		if (decltor != null && decltor.getName() != null) {
			name = decltor.getName().toString();
		}
		return name;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		//if (key == IPropertySource.class)
		//return new ASTPropertySource(getNode());

		return null;
	}

	public String getFilename()
	{
		if ( node == null ) return BLANK_STRING;
		IASTFileLocation f = node.getFileLocation();
		if( f == null )
			return BLANK_STRING;
		return f.getFileName();
	}

	public int getOffset() {
		IASTFileLocation f = node.getFileLocation();
		if( f == null )
			return 0;
		return f.getNodeOffset();
	}

	public int getLength() {
		IASTFileLocation f = node.getFileLocation();
		if( f == null )
			return 0;
		return f.getNodeLength();
	}

	public void setFiltersFlag(int flag) {
		filterFlag |= flag;

		if (parent != null ) {
			parent.setFiltersFlag(flag);
		}
	}

	public int getFiltersFlag() {
		return filterFlag;
	}

	public int relativeNodePosition( IASTNode n ){
		ASTNode astNode = (ASTNode) n;
		ASTNode thisNode = (ASTNode) getNode();
		if( thisNode.getOffset() > astNode.getOffset() )
			return -1;
		if( (thisNode.getOffset() + thisNode.getLength()) < (astNode.getOffset() + astNode.getLength()) )
			return 1;
		return 0;
	}
}
