/*
 * jETeL/CloverETL - Java based ETL application framework.
 * Copyright (c) Javlin, a.s. (info@cloveretl.com)
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jetel.component.tree.reader.mappping;

import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import org.jetel.metadata.DataFieldMetadata;
import org.jetel.metadata.DataRecordMetadata;

/**
 * Adds implicit mapping to TreeReader mapping.
 * Implicit mapping is created as follows: 
 * 	 - for each "Context" mapping element C with output port P assigned:
 * 		- for each metadata field with name N of edge on port P,
 *        which is NOT used in any C's child "Mapping" element, or generated key, or sequence field:
 *         - add synthetic "Mapping" child element with xpath="N" and cloverField="N" to the context C.
 * 
 * @author tkramolis (info@cloveretl.com) (c) Javlin, a.s. (www.cloveretl.com)
 *
 * @created Mar 22, 2012
 */
public class ImplicitMappingAddingVisitor implements MappingVisitor {
	
	private List<DataRecordMetadata> outPortsMetadata;
	private Stack<BitSet> contextFieldUsageStack = new Stack<BitSet>();
	private Stack<DataRecordMetadata> contextMetadata = new Stack<DataRecordMetadata>();
	
	public ImplicitMappingAddingVisitor(List<DataRecordMetadata> outPortsMetadata) {
		this.outPortsMetadata = outPortsMetadata;
	}
	
	@Override
	public void visitBegin(MappingContext context) {
		DataRecordMetadata metadata = getPortMetadata(context);
		if (metadata != null) {
			contextFieldUsageStack.push(new BitSet(metadata.getNumFields()));
			contextMetadata.push(metadata);
			
			String[] keys = context.getGeneratedKeys();
			if (keys != null) {
				for (String fieldName : keys) {
					markFieldUsed(fieldName);
				}
			}
			
			String seqField = context.getSequenceField();
			if (seqField != null) {
				markFieldUsed(seqField);
			}
		}
	}

	private void markFieldUsed(String fieldName) {
		DataRecordMetadata metadata = contextMetadata.peek();
		DataFieldMetadata field = metadata.getField(fieldName);
		if (field != null) {
			contextFieldUsageStack.peek().set(field.getNumber());
		} else {
			throw new IllegalArgumentException("Field \"" + fieldName + "\" does not exist in metadata " + metadata.getName());
		}
	}
	
	@Override
	public void visit(FieldMapping mapping) {
		markFieldUsed(mapping.getCloverField());
	}
	
	@Override
	public void visitEnd(MappingContext context) {
		DataRecordMetadata metadata = contextMetadata.pop();
		BitSet fieldUsage = contextFieldUsageStack.pop();

		fieldUsage.flip(0, metadata.getNumFields());
		
		for (int i = fieldUsage.nextSetBit(0); i >= 0; i = fieldUsage.nextSetBit(i+1)) {
			String fieldName = metadata.getField(i).getName();
			FieldMapping fieldMapping = new FieldMapping();
			fieldMapping.setXPath(fieldName); // TODO noneName instead?
			fieldMapping.setCloverField(fieldName);
			context.addChild(fieldMapping);
		}
	}

	
	private DataRecordMetadata getPortMetadata(MappingContext context) {
		Integer portIndex = context.getOutputPort();
		if (portIndex == null) {
			return null;
		}
		if (portIndex >= outPortsMetadata.size()) {
			throw new IndexOutOfBoundsException("Output port index " + portIndex + " out of range [0, " + (outPortsMetadata.size() - 1) + "]");
			// TODO or ignore instead?
		}
		return outPortsMetadata.get(portIndex);
	}
}