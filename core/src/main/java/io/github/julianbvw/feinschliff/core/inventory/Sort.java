package io.github.julianbvw.feinschliff.core.inventory;

import io.github.julianbvw.feinschliff.core.config.Settings;

/**
 * Working out what a row of slots should look like once it is tidy.
 *
 * <p>This is the one thing in here that does not move items but describes
 * them, and it therefore says nothing about slots at all: four rows of numbers
 * go in, a list of stacks comes out, and whoever asked writes them back. That
 * keeps every version's idea of what an item is on the other side of the line,
 * and it is what lets the whole of it be checked against random inventories
 * without a game.
 *
 * <p>Two stacks are the same thing only when both the id and the number beside
 * it match. That is what keeps two differently worn pickaxes apart, and with
 * them everything later versions put in that field.
 */
public final class Sort {

	private Sort() {
	}

	public static boolean enabled() {
		return Settings.INVENTORY_SORT.on();
	}

	/**
	 * The stacks a row should hold, in the order it should hold them.
	 *
	 * <p>As long as one kind reports one maximum -- which is what a stack size
	 * is, a property of the item and not of the slot it sits in -- the answer
	 * is never longer than what went in: merging cannot make more stacks than
	 * it was given, and splitting a merged pile back up cannot either. Feed it
	 * two different maxima for one kind and that stops holding, so whoever
	 * writes the answer back counts it first.
	 *
	 * @return one {@code {id, metadata, size}} per stack
	 */
	public static int[][] plan(int[] ids, int[] metas, int[] sizes, int[] maxSizes, SortOrder order) {
		int slots = ids.length;

		int[] id = new int[slots];
		int[] meta = new int[slots];
		int[] total = new int[slots];
		int[] max = new int[slots];
		int kinds = 0;

		for (int i = 0; i < slots; i++) {
			if (sizes[i] <= 0) {
				continue;
			}

			int kind = -1;
			for (int k = 0; k < kinds; k++) {
				if (id[k] == ids[i] && meta[k] == metas[i]) {
					kind = k;
					break;
				}
			}

			if (kind < 0) {
				kind = kinds++;
				id[kind] = ids[i];
				meta[kind] = metas[i];
				max[kind] = maxSizes[i];
			} else if (maxSizes[i] < max[kind]) {
				// Two slots disagreeing about how much of one thing fits is
				// not something this version does, but the smaller answer is
				// the safe one either way.
				max[kind] = maxSizes[i];
			}

			total[kind] += sizes[i];
		}

		int[] at = order(order, id, meta, total, kinds);

		int stacks = 0;
		for (int k = 0; k < kinds; k++) {
			stacks += (total[k] + room(max[k]) - 1) / room(max[k]);
		}

		int[][] plan = new int[stacks][];
		int next = 0;
		for (int i = 0; i < kinds; i++) {
			int kind = at[i];
			int left = total[kind];
			int fits = room(max[kind]);
			while (left > 0) {
				int size = left < fits ? left : fits;
				plan[next++] = new int[] {id[kind], meta[kind], size};
				left -= size;
			}
		}

		return plan;
	}

	/** A kind that claims to hold nothing per stack would never be laid down at all. */
	private static int room(int max) {
		return max < 1 ? 1 : max;
	}

	/** The kinds in the order they should be laid out, as indices into them. */
	private static int[] order(SortOrder order, int[] id, int[] meta, int[] total, int kinds) {
		int[] at = new int[kinds];
		for (int k = 0; k < kinds; k++) {
			at[k] = k;
		}

		// Insertion sort: a chest is fifty-four slots, and a stable one line
		// longer beats a clever one nobody can check by eye.
		for (int i = 1; i < kinds; i++) {
			int kind = at[i];
			int j = i - 1;
			while (j >= 0 && before(order, id, meta, total, kind, at[j])) {
				at[j + 1] = at[j];
				j--;
			}
			at[j + 1] = kind;
		}

		return at;
	}

	private static boolean before(SortOrder order, int[] id, int[] meta, int[] total, int a, int b) {
		if (order == SortOrder.COUNT && total[a] != total[b]) {
			return total[a] > total[b];
		}
		if (id[a] != id[b]) {
			return id[a] < id[b];
		}
		return meta[a] < meta[b];
	}
}
