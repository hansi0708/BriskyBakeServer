package com.hv.briskybakeserver.Model;

import com.google.android.gms.tasks.Task;

public class Sender {
    public Task<String> to;
    public Notification notification;

    public Sender(Task<String> to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public Task<String> getTo() {
        return to;
    }

    public void setTo(Task<String> to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
