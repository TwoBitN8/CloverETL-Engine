/* Generated By:JJTree: Do not edit this line. CLVFVariableLiteral.java */

package org.jetel.interpreter.ASTnode;

import org.jetel.interpreter.ExpParser;
import org.jetel.interpreter.ParseException;
import org.jetel.interpreter.TransformLangParserVisitor;
	

public class CLVFVariableLiteral extends SimpleNode {
    
    public int varSlot;
    public String varName;
    public boolean localVar;
    public int varType;
    public int arrayIndex=-1;
    public String mapKey;
    public boolean indexSet=false;
    
  public CLVFVariableLiteral(int id) {
    super(id);
  }

  public CLVFVariableLiteral(ExpParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
  
  public void setVarSlot(int slot){
      this.varSlot=slot;
  }
  
  public void setVarName(String name){
      this.varName=name;
  }
  
  public void setLocalVariale(boolean isLocal){
      this.localVar=isLocal;
  }
  
  public void setVarType(int type) {
      this.varType=type;
  }
  
  public String toString(){
      return super.toString()+" name \""+varName+"\" type \""+varType+"\" slot "+varSlot+" local "+localVar;
  }

/**
 * @param arrayIndex the arrayIndex to set
 * @since 21.3.2007
 */
public void setArrayIndex(String sIndex) throws ParseException {
    try{
        this.arrayIndex=Integer.parseInt(sIndex);
    }catch(NumberFormatException ex){
        throw new ParseException("Error when parsing \"arrayIndex\" parameter value \""+sIndex+"\"");
    }
}

/**
 * @param mapKey the mapKey to set
 * @since 21.3.2007
 */
public void setMapKey(String mapKey) {
    this.mapKey = mapKey;
}
}
