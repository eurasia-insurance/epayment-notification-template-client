package tech.lapsa.epayment.notifier.beans.mdb;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;

import tech.lapsa.epayment.domain.Invoice;
import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.ObjectConsumerListener;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public abstract class AOrderNotificationDrivenBean<T extends Invoice> extends ObjectConsumerListener<T> {

    AOrderNotificationDrivenBean(final Class<T> objectClazz) {
	super(objectClazz);
    }

    protected abstract Locale locale(Invoice invoice);

    @Resource(lookup = JNDI_RESOURCE_CONFIGURATION)
    private Properties configurationProperties;

    @Override
    protected void accept(final T invoice, final Properties properties) {
	MyObjects.requireNonNull(invoice, "invoice");

	invoice.unlazy();
	final TextModel textModel = TextFactory.newModelBuilder() //
		.withLocale(locale(invoice)) //
		.bind("instanceVerb", configurationProperties.getProperty(PROPERTY_INSTANCE_VERB, "")) //
		.bind("invoice", invoice) //
		.bind("order", invoice) //
		.bind("paymentUrl", properties.getProperty("paymentUrl")) //
		.bindProperties(properties) //
		.build();

	sendWithModel(textModel, invoice);
    }

    protected abstract void sendWithModel(TextModel textModel, T order);
}
