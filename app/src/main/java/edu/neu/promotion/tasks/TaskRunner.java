package edu.neu.promotion.tasks;

public class TaskRunner {

    private static final int TASK_STATE_READY = 0;
    private static final int TASK_STATE_RUNNING = 1;
    private static final int TASK_STATE_SUCCESS = 2;
    private static final int TASK_STATE_FAILED = 3;

    public interface Callback {
        void onTaskSuccess(Task which, Object result);
        void onTaskFailed(Task which);
        void onTaskRetryFailed(Task which);
    }

    private final Task task;
    private final int autoRetryTimes;
    private int taskState;
    private final Callback callback;

    public TaskRunner(Task task, int autoRetryTimes, Callback callback) {
        this.task = task;
        this.autoRetryTimes = autoRetryTimes;
        taskState = TASK_STATE_READY;
        this.callback = callback;
    }

    private final class TaskRunnable implements Runnable {

        private final boolean retry;

        private TaskRunnable(boolean retry) {
            this.retry = retry;
        }

        @Override
        public void run() {
            boolean returnValue;
            taskState = TASK_STATE_RUNNING;
            //第一次
            if (retry) {
                returnValue = task.onRetry();
            }
            else {
                returnValue = task.onExecute();
            }
            if (taskState != TASK_STATE_RUNNING) {
                return;
            }
            //接下来
            int retryCounter = 0;
            while (!returnValue && retryCounter < autoRetryTimes) {
                if (callback != null) {
                    callback.onTaskFailed(task);
                }
                returnValue = task.onRetry();
                if (taskState != TASK_STATE_RUNNING) {
                    return;
                }
                retryCounter++;
            }
            if (returnValue) {
                taskState = TASK_STATE_SUCCESS;
                if (callback != null) {
                    callback.onTaskSuccess(task, task.onGetResult());
                }
            }
            else {
                taskState = TASK_STATE_FAILED;
                if (callback != null) {
                    callback.onTaskFailed(task);
                    callback.onTaskRetryFailed(task);
                }
            }
        }
    }

    public void execute() {
        switch (taskState) {
            case TASK_STATE_READY:
                new Thread(new TaskRunnable(false)).start();
                break;
            case TASK_STATE_RUNNING:
                throw new IllegalStateException("该任务已经在运行。");
            case TASK_STATE_SUCCESS:
                throw new IllegalStateException("该任务已运行成功。");
            case TASK_STATE_FAILED:
                new Thread(new TaskRunnable(true)).start();
                break;
        }
    }

    public void cancel() {
        if (taskState == TASK_STATE_RUNNING) {
            taskState = TASK_STATE_READY;
            task.onCancel();
        }
    }

    public <T> T getResult() {
        if (taskState != TASK_STATE_SUCCESS) {
            throw new IllegalStateException("此任务尚未成功执行，不能获取result。");
        }
        return (T) task.onGetResult();
    }
}
