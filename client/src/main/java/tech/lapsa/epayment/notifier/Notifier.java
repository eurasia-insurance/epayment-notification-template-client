package tech.lapsa.epayment.notifier;

import com.lapsa.kkb.core.KKBOrder;

public interface Notifier {

    @Deprecated
    default void assignOrderNotification(NotificationChannel channel, NotificationRecipientType recipientType,
	    NotificationRequestStage stage,
	    KKBOrder order) {
	newNotificationBuilder() //
		.withChannel(channel) //
		.withEvent(stage) //
		.withRecipient(recipientType) //
		.forEpayment(order) //
		.build() //
		.send();
    }

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
