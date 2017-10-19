package tech.lapsa.epayment.notifier.beans.mdb;

import static tech.lapsa.epayment.notifier.beans.Constants.*;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.lapsa.kkb.core.KKBOrder;
import com.lapsa.kkb.services.KKBFactory;

import tech.lapsa.java.commons.function.MyObjects;
import tech.lapsa.javax.jms.ObjectConsumerListener;
import tech.lapsa.lapsa.text.TextFactory;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder;
import tech.lapsa.lapsa.text.TextFactory.TextModelBuilder.TextModel;

public abstract class AOrderNotificationDrivenBean<T extends KKBOrder> extends ObjectConsumerListener<T> {

    AOrderNotificationDrivenBean(final Class<T> objectClazz) {
	super(objectClazz);
    }

    protected abstract Locale locale(KKBOrder order);

    @Resource(lookup = JNDI_RESOURCE_CONFIGURATION)
    private Properties configurationProperties;

    @Inject
    private KKBFactory kkbFactory;

    @Override
    protected void accept(T order) {
	MyObjects.requireNonNull(order, "order");

	TextModelBuilder builder = TextFactory.newModelBuilder() //
		.withLocale(locale(order)) //
		.bind("instanceVerb", configurationProperties.getProperty(PROPERTY_INSTANCE_VERB, "")) //
		.bind("order", order) //
		.bind("paymentUrl", kkbFactory.generateDefaultPaymentURI(order.getId()).toString());

	TextModel textModel = builder.build();
	sendWithModel(textModel, order);
    }

    protected abstract void sendWithModel(TextModel textModel, T order);
}
