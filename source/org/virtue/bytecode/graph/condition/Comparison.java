package org.virtue.bytecode.graph.condition;

import org.virtue.bytecode.element.FieldElement;
import org.virtue.utility.Value;

/**
 * @author : const_
 */
public class Comparison<T> {

    private Value<T> constant;
    private FieldElement field;
    private Condition condition;
    private String operator;

    public Comparison(Value<T> constant, FieldElement field, Condition condition, String operator) {
        this.constant = constant;
        this.field = field;
        this.condition = condition;
        this.operator = operator;
    }

    public String operator() {
        return operator;
    }

    public Value<T> constant() {
        return constant;
    }

    public FieldElement field() {
        return field;
    }

    public Condition condition() {
        return condition;
    }
}
