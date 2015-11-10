package org.freshgrade.molr.test;

import java.util.ArrayList;
import java.util.Iterator;


import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;

import lombok.Getter;

public class ListBackedCursor<TResult> implements MongoCursor<TResult> {

	private Iterator<TResult> iterator;
	
	@Getter
	private ArrayList<TResult> arrayList;

	public ListBackedCursor(ArrayList<TResult> list) {
		arrayList = list;
		iterator = list.iterator();
	}
	
	@Override
	public void close() {
		
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public TResult next() {
		return iterator.next();
	}

	@Override
	public TResult tryNext() {
		if(hasNext()){
			return next();
		} else {
			return null;
		}
	}

	@Override
	public ServerCursor getServerCursor() {
		return null;
	}

	@Override
	public ServerAddress getServerAddress() {
		return null;
	}

}
