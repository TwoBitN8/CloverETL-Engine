/* Generated By:JJTree: Do not edit this line. CLVFIfStatement.java */

package org.jetel.interpreter.ASTnode;
import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.TransformLangParserVisitor;
public class CLVFIfStatement extends SimpleNode {
  public CLVFIfStatement(int id) {
    super(id);
  }

  public CLVFIfStatement(ExpParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
