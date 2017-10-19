package tech.lapsa.epayment.notifier;

import com.lapsa.kkb.core.KKBOrder;

public interface Notifier {

    void assignOrderNotification(NotificationChannel channel, NotificationRecipientType recipientType,
	    NotificationRequestStage stage,
	    KKBOrder order);

    NotificationBuilder newNotificationBuilder();

    interface NotificationBuilder {

	NotificationBuilder withChannel(NotificationChannel channel);

	NotificationBuilder withRecipient(NotificationRecipientType recipientType);

	NotificationBuilder withEvent(NotificationRequestStage stage);

	NotificationBuilder forEpayment(KKBOrder request);

	Notification build();

	interface Notification {

	    void send();

	}

    }
}
