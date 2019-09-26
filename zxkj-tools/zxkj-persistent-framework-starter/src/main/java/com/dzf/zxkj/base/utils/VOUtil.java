package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.model.CircularlyAccessibleValueObject;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VOUtil {
	public static final int ASC = 1;
	public static final int DESC = -1;
	
	public static <T extends SuperVO> void ascSort(List<T> list, String[] fields) {
		if (list == null || list.size() == 0 || list.size() == 1)
			return;
		if ((fields == null) || (fields.length == 0))
			return;
		int[] ascFlags = new int[fields.length];
		Arrays.fill(ascFlags, 1);
		sort(list, fields, ascFlags);
	}

	public static <T extends SuperVO> void descSort(List<T> list,
			String[] fields) {
		if (list == null || list.size() == 0 || list.size() == 1)
			return;
		if ((fields == null) || (fields.length == 0))
			return;
		int[] ascFlags = new int[fields.length];
		Arrays.fill(ascFlags, -1);
		sort(list, fields, ascFlags);
	}

	public static void ascSort(CircularlyAccessibleValueObject[] vos,
							   String[] fields) {
		if (vos == null || vos.length == 0 || vos.length == 1)
			return;
		if ((fields == null) || (fields.length == 0))
			return;
		int[] ascFlags = new int[fields.length];
		Arrays.fill(ascFlags, 1);
		sort(vos, fields, ascFlags);
	}

	public static void descSort(CircularlyAccessibleValueObject[] vos,
			String[] fields) {
		if (vos == null || vos.length == 0 || vos.length == 1)
			return;
		if ((fields == null) || (fields.length == 0))
			return;
		int[] ascFlags = new int[fields.length];
		Arrays.fill(ascFlags, -1);
		sort(vos, fields, ascFlags);
	}

//	public static String getCombinesKey(CircularlyAccessibleValueObject vo,
//			String[] groupFields) {
//		if (vo == null)
//			throw new IllegalArgumentException(
//					"AggVOSummarize.getCombinesKey vo cann't be null");
//		if ((groupFields == null) || (groupFields.length == 0))
//			throw new IllegalArgumentException(
//					"AggVOSummarize.getCombinesKey groupFields cann't be null or empty");
//		StringBuffer result = new StringBuffer();
//		for (int i = 0; i < groupFields.length; i++) {
//			result.append("" + vo.getAttributeValue(groupFields[i]));
//		}
//		return result.toString();
//	}

//	public static CircularlyAccessibleValueObject max(
//			CircularlyAccessibleValueObject[] vos, String fieldname) {
//		if ((vos == null) || (vos.length == 0))
//			return null;
//		int maxindex = 0;
//		for (int i = 1; i < vos.length; i++) {
//			Object o = vos[i].getAttributeValue(fieldname);
//			if (o != null) {
//				String c = o.toString();
//				if (vos[maxindex].getAttributeValue(fieldname) == null) {
//					maxindex = i;
//				} else {
//					String s = "" + vos[maxindex].getAttributeValue(fieldname);
//					Double dc = new Double(c);
//					Double ds = new Double(s);
//
//					if (dc.compareTo(ds) > 0)
//						maxindex = i;
//				}
//			}
//		}
//		return vos[maxindex];
//	}

	public static void sort(CircularlyAccessibleValueObject[] vos,
			final String[] fields, final int[] ascFlags, final boolean nullLast) {
		if (vos == null || vos.length == 0 || vos.length == 1)
			return;
		if ((fields == null) || (fields.length == 0))
			return;
		if (ascFlags == null)
			throw new IllegalArgumentException(
					"VOUtil.sort ascFlags cann't be null");
		if (fields.length != ascFlags.length) {
			throw new IllegalArgumentException(
					"VOUtil.sort length of fields not equal with that of ascFlags");
		}
		for (int i = 0; i < ascFlags.length; i++) {
			if ((ascFlags[i] != 1) && (ascFlags[i] != -1)) {
				throw new IllegalArgumentException(
						"VOUtil.sort Illegal Value of ascFlag i=" + i
								+ " value= " + ascFlags[i]);
			}
		}
		Comparator c = new Comparator() {
			public int compare(Object o1, Object o2) {
				CircularlyAccessibleValueObject vo1 = (CircularlyAccessibleValueObject) o1;
				CircularlyAccessibleValueObject vo2 = (CircularlyAccessibleValueObject) o2;

				int Greater = 1;
				int Less = -1;
				int Equal = 0;

				for (int i = 0; i < fields.length; i++) {
					Object v1 = vo1.getAttributeValue(fields[i]);
					Object v2 = vo2.getAttributeValue(fields[i]);

					if ((v1 != null) || (v2 != null)) {
						if ((v1 == null) && (v2 != null)) {
							if ((ascFlags[i] == 1) && (nullLast)) {
								return ascFlags[i] * Greater;
							}
							return ascFlags[i] * Less;
						}
						if ((v1 != null) && (v2 == null)) {
							if ((ascFlags[i] == 1) && (nullLast)) {
								return ascFlags[i] * Less;
							}
							return ascFlags[i] * Greater;
						}

						Comparable c1 = null;
						Comparable c2 = null;

						if (((v1 instanceof Comparable))
								&& ((v2 instanceof Comparable))) {
							c1 = (Comparable) v1;
							c2 = (Comparable) v2;
						} else {
							if (((v1 instanceof DZFDouble))
									&& ((v2 instanceof DZFDouble))) {
								DZFDouble u1 = (DZFDouble) v1;
								DZFDouble u2 = (DZFDouble) v2;
								if (u1.compareTo(u2) == 0) {
									continue;
								}
								return u1.compareTo(u2) * ascFlags[i];
							}

							c1 = "" + v1;
							c2 = "" + v2;
						}

						if (c1.compareTo(c2) != 0) {
							return c1.compareTo(c2) * ascFlags[i];
						}
					}
				}
				return Equal;
			}
		};
		Arrays.sort(vos, c);
	}
	
	public static <T extends SuperVO> void sort(List<T> list,
			final String[] fields, final int[] ascFlags, final boolean nullLast) {
		if (list == null || list.size() == 0 || list.size() == 1)
			return;
		if ((fields == null) || (fields.length == 0))
			return;
		if (ascFlags == null)
			throw new IllegalArgumentException(
					"VOUtil.sort ascFlags cann't be null");
		if (fields.length != ascFlags.length) {
			throw new IllegalArgumentException(
					"VOUtil.sort length of fields not equal with that of ascFlags");
		}
		for (int i = 0; i < ascFlags.length; i++) {
			if ((ascFlags[i] != 1) && (ascFlags[i] != -1)) {
				throw new IllegalArgumentException(
						"VOUtil.sort Illegal Value of ascFlag i=" + i
								+ " value= " + ascFlags[i]);
			}
		}
		Comparator c = new Comparator() {
			public int compare(Object o1, Object o2) {
				CircularlyAccessibleValueObject vo1 = (CircularlyAccessibleValueObject) o1;
				CircularlyAccessibleValueObject vo2 = (CircularlyAccessibleValueObject) o2;

				int Greater = 1;
				int Less = -1;
				int Equal = 0;

				for (int i = 0; i < fields.length; i++) {
					Object v1 = vo1.getAttributeValue(fields[i]);
					Object v2 = vo2.getAttributeValue(fields[i]);

					if ((v1 != null) || (v2 != null)) {
						if ((v1 == null) && (v2 != null)) {
							if ((ascFlags[i] == 1) && (nullLast)) {
								return ascFlags[i] * Greater;
							}
							return ascFlags[i] * Less;
						}
						if ((v1 != null) && (v2 == null)) {
							if ((ascFlags[i] == 1) && (nullLast)) {
								return ascFlags[i] * Less;
							}
							return ascFlags[i] * Greater;
						}

						Comparable c1 = null;
						Comparable c2 = null;

						if (((v1 instanceof Comparable))
								&& ((v2 instanceof Comparable))) {
							c1 = (Comparable) v1;
							c2 = (Comparable) v2;
						} else {
							if (((v1 instanceof DZFDouble))
									&& ((v2 instanceof DZFDouble))) {
								DZFDouble u1 = (DZFDouble) v1;
								DZFDouble u2 = (DZFDouble) v2;
								if (u1.compareTo(u2) == 0) {
									continue;
								}
								return u1.compareTo(u2) * ascFlags[i];
							}

							c1 = "" + v1;
							c2 = "" + v2;
						}

						if (c1.compareTo(c2) != 0) {
							return c1.compareTo(c2) * ascFlags[i];
						}
					}
				}
				return Equal;
			}
		};
		Collections.sort(list, c);
	}

	public static void sort(CircularlyAccessibleValueObject[] vos,
			String[] fields, int[] ascFlags) {
		sort(vos, fields, ascFlags, false);
	}
	
	public static <T extends SuperVO> void sort(List<T> list,
			String[] fields, int[] ascFlags) {
		sort(list, fields, ascFlags, false);
	}
}