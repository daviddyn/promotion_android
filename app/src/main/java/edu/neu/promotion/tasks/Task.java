package edu.neu.promotion.tasks;

public interface Task {

    boolean onExecute();

    boolean onRetry();

    void onCancel();

    Object onGetResult();
}
