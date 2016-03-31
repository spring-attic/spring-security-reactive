package reactive.client;

public interface RequestPostProcessor {

	ExtendedDefaultHttpRequestBuilder postProcess(ExtendedDefaultHttpRequestBuilder request);
}
