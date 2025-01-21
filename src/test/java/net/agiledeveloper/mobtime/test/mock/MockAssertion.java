package net.agiledeveloper.mobtime.test.mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MockAssertion {

    public static AssertMock expectThat(Mock mock) {
        return new AssertMock(mock);
    }


    public static class AssertMock {

        private final Mock mock;


        public AssertMock(Mock mock) {
            this.mock = mock;
        }


        public AssertMock wasNeverCalled() {
            assertThat(mock.wasNeverCalled()).isTrue();
            return this;
        }

        public AssertMock wasCalledOnce() {
            assertThat(mock.wasCalledOnce()).isTrue();
            return this;
        }

    }

}

