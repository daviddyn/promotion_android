package edu.neu.promotion.components;

import java.util.HashMap;

import edu.neu.promotion.tasks.Task;
import edu.neu.promotion.tasks.TaskRunner;

public class RunTaskPage extends Page {

    private final HashMap<Integer, TaskRunner> tasks;

    private final class TaskSuccessPoster implements Runnable {

        private final int requestCode;
        private final Object result;

        private TaskSuccessPoster(int requestCode, Object result) {
            this.requestCode = requestCode;
            this.result = result;
        }

        @Override
        public void run() {
            if (tasks.remove(requestCode) != null) {
                onTaskFinish(requestCode);
                onTaskResult(requestCode, result);
            }
        }
    }

    private final class TaskFailedPoster implements Runnable {

        private final int requestCode;
        private final int currentRetryTimes;

        private TaskFailedPoster(int requestCode, int currentRetryTimes) {
            this.requestCode = requestCode;
            this.currentRetryTimes = currentRetryTimes;
        }

        @Override
        public void run() {
            if (tasks.containsKey(requestCode)) {
                onTaskFailed(requestCode, currentRetryTimes);
            }
        }
    }

    private final class TaskRetryFailedPoster implements Runnable {

        private final int requestCode;

        private TaskRetryFailedPoster(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void run() {
            if (tasks.containsKey(requestCode)) {
                onTaskFinish(requestCode);
                onTaskRetryFailed(requestCode);
            }
        }
    }

    private final class TaskCallback implements TaskRunner.Callback {

        private final int requestCode;

        private TaskCallback(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onTaskSuccess(Task which, Object result) {
            post(new TaskSuccessPoster(requestCode, result));
        }

        @Override
        public void onTaskFailed(Task which, int currentRetryTimes) {
            post(new TaskFailedPoster(requestCode, currentRetryTimes));
        }

        @Override
        public void onTaskRetryFailed(Task which) {
            post(new TaskRetryFailedPoster(requestCode));
        }
    }

    public RunTaskPage(PageManager pageManager, Object... args) {
        super(pageManager, args);
        tasks = new HashMap<>();
    }

    @Override
    protected void onDestroy() {
        //????????????Task
        for (TaskRunner t : tasks.values()) {
            t.cancel();
        }
        super.onDestroy();
    }

    protected final void runTask(Task task, int requestCode) {
        runTask(task, 0, requestCode);
    }

    protected final void runTask(Task task, int autoRetryTimes, int requestCode) {
        //???????????????RequestCode?????????????????????????????????????????????
        TaskRunner taskRunner = tasks.get(requestCode);
        if (taskRunner != null) {
            taskRunner.cancel();
        }
        taskRunner = new TaskRunner(task, autoRetryTimes, new TaskCallback(requestCode));
        tasks.put(requestCode, taskRunner);
        onTaskBegin(requestCode);
        taskRunner.execute();
    }

    protected final void retryTask(int requestCode) {
        TaskRunner taskRunner = tasks.get(requestCode);
        if (taskRunner == null) {
            return;
        }
        onTaskBegin(requestCode);
        taskRunner.execute();
    }

    protected final void cancelTask(int requestCode) {
        TaskRunner taskRunner = tasks.remove(requestCode);
        if (taskRunner != null) {
            taskRunner.cancel();
        }
    }

    protected final void cancelAllTasks() {
        TaskRunner[] taskRunners = new TaskRunner[tasks.size()];
        tasks.values().toArray(taskRunners);
        tasks.clear();
        for (TaskRunner t : taskRunners) {
            t.cancel();
        }
    }

    protected final boolean isRunning(int requestCode) {
        return tasks.containsKey(requestCode);
    }

    /**
     * ????????????????????????
     */
    protected void onTaskBegin(int requestCode) {};

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????cancelTask()??????????????????????????????????????????
     */
    protected void onTaskFinish(int requestCode) {};

    /**
     * ????????????????????????
     */
    protected void onTaskResult(int requestCode, Object result) {};

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     */
    protected void onTaskFailed(int requestCode, int currentRetryTimes) {};

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    protected void onTaskRetryFailed(int requestCode) {}
}