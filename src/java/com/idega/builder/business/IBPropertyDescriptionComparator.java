package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBPropertyDescriptionComparator implements java.util.Comparator{

    public IBPropertyDescriptionComparator(){
    }

    public int compare(Object o1,Object o2){
      return compare((IBPropertyDescription)o1,(IBPropertyDescription)o2);
    }

    public int compare(IBPropertyDescription p1,IBPropertyDescription p2){
      String s1 = p1.getMethodDescription();
      String s2 = p2.getMethodDescription();
      return s1.compareTo(s2);
    }


    public static IBPropertyDescriptionComparator getInstance(){
      return new IBPropertyDescriptionComparator();
    }

}
