package dev.lambdaurora.lambdynlights.eventbus;

import org.apache.commons.lang3.reflect.MethodUtils;
import dev.lambdaurora.lambdynlights.event.Event;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class Subscriber {

	private final Consumer<Object> subscriberCaller;
	private final Class<? extends Event> eventClass;
	private final Class<?> targetClass;
	private final String signature;

	public Subscriber(Object target, String methodName, Class<? extends Event> eventClass) {
		this(target, MethodUtils.getAccessibleMethod(target.getClass(), methodName, eventClass), eventClass);
	}

	public Subscriber(Object target, Method method) {
		this(target, method, getEvent(method));
	}

	public Subscriber(Object target, Method method, Class<? extends Event> eventClass) {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			CallSite callsite = LambdaMetafactory.metafactory(lookup,
					"accept",
					MethodType.methodType(Consumer.class, target.getClass()),
					MethodType.methodType(void.class, Object.class),
					lookup.unreflect(method),
					//lookup.findVirtual(target.getClass(), methodName, MethodType.methodType(void.class, eventClass)),
					MethodType.methodType(void.class, eventClass));

			subscriberCaller = (Consumer<Object>) callsite.getTarget().invokeWithArguments(target);

			this.eventClass = eventClass;
			this.targetClass = target.getClass();
			this.signature = targetClass.getName() + "." + method.getName() + "(" + eventClass.getName() + ")";
		} catch (Throwable t) {
			// Yea, we got a problem
			throw new RuntimeException(t);
		}
	}

	private static Class<? extends Event> getEvent(Method method) {
		Parameter[] parameters = method.getParameters();
		if (parameters.length == 0 || !Event.class.isAssignableFrom(parameters[0].getType())) {
			throw new RuntimeException("Tried to create Subscriber with invalid parameters");
		}

		return (Class<? extends Event>) parameters[0].getType();
	}

	public void callSubscriber(Event event) {
		subscriberCaller.accept(event);
	}

	public Class<? extends Event> getEventClass() {
		return eventClass;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public String getSignature() {
		return signature;
	}

	@Override
	public final int hashCode() {
		return signature.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Subscriber) {
			Subscriber that = (Subscriber) obj;
			return signature.equals(that.signature);
		}

		return false;
	}
}
