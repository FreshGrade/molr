package org.freshgrade.molr.test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.CursorType;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

public class ListBackedFindIterable<T> implements FindIterable<T>{

	private ListBackedCursor<T> listBackedCursor;

	public ListBackedFindIterable(ListBackedCursor<T> list){
		listBackedCursor = list;
	}
	
	@Override
	public MongoCursor<T> iterator() {
		return listBackedCursor;
	}

	@Override
	public T first() {
		return this.listBackedCursor.getArrayList().get(0);
	}

	@Override
	public <U> MongoIterable<U> map(Function<T, U> mapper) {
		throw new IllegalStateException();
	}

	@Override
	public void forEach(Block<? super T> block) {
		throw new IllegalStateException();
	}

	@Override
	public <A extends Collection<? super T>> A into(A target) {
		throw new IllegalStateException();
	}

	@Override
	public FindIterable<T> filter(Bson filter) {
		return this;
	}

	@Override
	public FindIterable<T> limit(int limit) {
		return this;
	}

	@Override
	public FindIterable<T> skip(int skip) {
		return this;
	}

	@Override
	public FindIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
		return this;
	}

	@Override
	public FindIterable<T> modifiers(Bson modifiers) {
		return this;
	}

	@Override
	public FindIterable<T> projection(Bson projection) {
		return this;	
	}

	@Override
	public FindIterable<T> sort(Bson sort) {
		return this;
	}

	@Override
	public FindIterable<T> noCursorTimeout(boolean noCursorTimeout) {
		return this;
	}

	@Override
	public FindIterable<T> oplogReplay(boolean oplogReplay) {
		return this;
	}

	@Override
	public FindIterable<T> partial(boolean partial) {
		return this;
	}

	@Override
	public FindIterable<T> cursorType(CursorType cursorType) {
		return this;
	}

	@Override
	public FindIterable<T> batchSize(int batchSize) {
		return this;
	}

}
