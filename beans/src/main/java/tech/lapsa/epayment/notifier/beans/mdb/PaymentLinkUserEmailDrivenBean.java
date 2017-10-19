package tech.lapsa.epayment.notifier.beans.mdb;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.Locale;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import com.lapsa.kkb.core.KKBOrder;

import tech.lapsa.epayment.notifier.beans.NotificationMessages;
import tech.lapsa.epayment.notifier.beans.NotificationTemplates;
import tech.lapsa.epayment.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;

@MessageDriven(mappedName = JNDI_JMS_DEST_PAYMENTLINK_REQUESTER_EMAIL)
public class PaymentLinkUserEmailDrivenBean extends AEmailRequestNotificationDrivenBean<KKBOrder> {

    @Inject
    @QRecipientUser
    protected MailFactory mailFactory;

    public PaymentLinkUserEmailDrivenBean() {
	super(KKBOrder.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(MailMessageBuilder builder, KKBOrder request) throws MailBuilderException {
	return builder.withTORecipient(request.getConsumerEmail(), request.getConsumerName());
    }

    @Override
    protected Locale locale(KKBOrder request) {
	return request.getConsumerLanguage().getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.KKB_ORDER_PAYMENT_LINK_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.PAYMENT_LINK_NOTIFICATION_TEMPLATE;
    }
}
