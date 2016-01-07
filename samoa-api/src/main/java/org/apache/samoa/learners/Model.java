package org.apache.samoa.learners;

import org.apache.samoa.instances.Instance;

import java.io.Serializable;

public interface Model extends Serializable {
    double[] predict(Instance inst);
}
