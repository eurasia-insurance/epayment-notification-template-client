package tech.lapsa.epayment.notifier.beans.mdb;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.epayment.notifier.beans.NotificationMessages;
import tech.lapsa.epayment.notifier.beans.NotificationTemplates;
import tech.lapsa.epayment.notifier.beans.qualifiers.QRecipientUser;
import tech.lapsa.javax.mail.MailBuilderException;
import tech.lapsa.javax.mail.MailFactory;
import tech.lapsa.javax.mail.MailMessageBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;

@MessageDriven(mappedName = JNDI_JMS_DEST_PAYMENTSUCCESS_REQUESTER_EMAIL)
public class PaymentSuccessUserEmailDrivenBean extends EmailInvoiceNotificationBase<Invoice> {

    @Inject
    @QRecipientUser
    protected MailFactory mailFactory;

    public PaymentSuccessUserEmailDrivenBean() {
	super(Invoice.class);
    }

    @Override
    protected MailFactory mailFactory() {
	return mailFactory;
    }

    @Override
    protected MailMessageBuilder recipients(final MailMessageBuilder builder, final Invoice invoice)
	    throws MailBuilderException {
	return builder.withTORecipient(invoice.getConsumerEmail(), invoice.getConsumerName());
    }

    @Override
    protected Locale locale(final Invoice invoice) {
	return invoice.getConsumerPreferLanguage().getLocale();
    }

    @Override
    protected NotificationMessages getSubjectTemplate() {
	return NotificationMessages.PAYMENT_SUCCESS_SUBJECT;
    }

    @Override
    protected NotificationTemplates getBodyTemplate() {
	return NotificationTemplates.PAYMENT_SUCCESS_TEMPLATE;
    }

    @Override
    protected TextModelBuilder updateTextModel(TextModelBuilder textModelBuilder, Invoice invoice,
	    Properties properties) {
	return textModelBuilder;
    }

}
