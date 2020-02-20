package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class T1 {

	public static void main(String[] args) {
//		m2();
//		m3();
		HashMap<String, String>map=new HashMap<>();
		map.put("11", "aaa");
		map.put("22", "bbb");
//		Set<String>set=map.keySet();
//		Collection<String>co=map.values();
		
				
		
		Date a=new Date();
		
		
		
		
		
		
		
		
		
		
		
	}

	private static void m3() {
		HashSet<String> set=new HashSet<String>();
		set.add("aaa");
		set.add("bbb");
		set.add("ccc");
		
		for (String string : set) {
			System.out.println(string);
		}
		ArrayList<String> list=new ArrayList<String>();
		list.add("aa");
		list.add("bbb");
		
		System.out.println(Collections.binarySearch(list, "aa"));
	}

	private static void m2() {
		ArrayList<String> list=new ArrayList<String>();
		list.add("aa");
		list.add(0, "bbb");
//		m1(list);
		
//		Iterator< String>it=list.iterator();
//		while(it.hasNext()) {
//			System.out.println(it.next());
//		}
		
		for (String string : list) {
			System.out.println(string);
		}
	}

	private static void m1(ArrayList<String> list) {
		System.out.println(list.toString());
		System.out.println(list.contains("bbb"));
		System.out.println(list.contains("ccc"));
		Object []sl=list.toArray();
		
		
		ArrayList<String> list2=new ArrayList<String>();
		list2.add("aa");
		list2.add(0, "bbb");
		
		System.out.println(list.equals(list2));
		System.out.println(list.get(1));
		System.out.println(list.remove(1));
		System.out.println(list.set(0, "fff"));
		
		System.out.println(list);
	}

}
