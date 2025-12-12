package io.github.ngspace.hudder.data_management;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class LimitedRefreshSpeedData<T> {
	
	Instant lastupdate = Instant.now();
	T data;
	Supplier<T> updater;
	int msdiff;
	
	public LimitedRefreshSpeedData(Supplier<T> updater, int msdiff) {
		this.updater = updater;
		this.msdiff = msdiff;
		this.data = updater.get();
	}
	
	public T get() {
		Instant now = Instant.now();
		if (Duration.between(lastupdate, now).toMillis()>msdiff) {//Has the data timed out?
			data = updater.get();
			lastupdate = Instant.now();
		}
		return data;
	}
}
