package mobtime.test;

public abstract class BaseMock {

    private int callCount = 0;


    public boolean wasNeverCalled() {
        return wasCalledNTimes(0);
    }

    public boolean wasCalledOnce() {
        return wasCalledNTimes(1);
    }

    public boolean wasCalledNTimes(int n) {
        return callCount == n;
    }

    protected int getCallCount() {
        return callCount;
    }


    protected void incrementCallCount() {
        callCount++;
    }

}
