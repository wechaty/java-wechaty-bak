package io.github.wechaty.schemas;

public class Event {

    enum ScanStatus{
        Unknown(-1),

        Cancle(0),

        Waiting(1),
        Scanned(2),
        Cnnfirmed(3),
        Timeout(4);
        private int code;

        ScanStatus(int code) {
            this.code = code;
        }
    }

}
