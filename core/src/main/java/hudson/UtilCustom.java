/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import hudson.model.AbstractBuild;
import hudson.model.Executor;
import hudson.model.queue.Executables;
import hudson.model.queue.SubTask;
import hudson.model.queue.WorkUnit;
import hudson.model.Build;
import hudson.model.Queue;
import hudson.model.Queue.BlockedItem;
import hudson.model.Queue.BuildableItem;
import hudson.model.Queue.Executable;
import hudson.model.Queue.Item;
import hudson.model.Queue.JobOffer;
import hudson.model.Queue.WaitingItem;
import hudson.scm.RevisionParameterAction;
import hudson.scm.SubversionSCM.SvnInfo;
import jenkins.model.Jenkins;

/**
 * Various utility methods that don't have more proper home.
 * 
 */
public class UtilCustom {

	public static String getInfoAboutParked(Map<Executor, JobOffer> map) {
		StringBuffer sb = new StringBuffer();
		for (Entry<Executor, JobOffer> av : map.entrySet()) {
			WorkUnit wu = av.getValue().getWorkUnit();
			if (wu != null) {
				sb.append(getInfoAboutItem(wu.context.item) + " | ");
			}
		}
		return sb.toString();
	}

	public static String getInfoAboutListBlocked(List<BlockedItem> list) {
		StringBuffer sb = new StringBuffer();
		for (Item item : list) {
			sb.append(getInfoAboutItem(item) + " | ");
		}
		return sb.toString();
	}

	public static String getInfoAboutSet(Set<WaitingItem> set) {
		StringBuffer sb = new StringBuffer();
		for (Item item : set) {
			sb.append(getInfoAboutItem(item) + " | ");
		}
		return sb.toString();
	}

	public static String getInfoAboutList(List<BuildableItem> list) {
		StringBuffer sb = new StringBuffer();
		for (Item item : list) {
			sb.append(getInfoAboutItem(item) + " | ");
		}
		return sb.toString();
	}

	public static String getInfoAboutItemList(List<Queue.Item> list) {
		StringBuffer sb = new StringBuffer();
		for (Item item : list) {
			sb.append(getInfoAboutItem(item) + " | ");
		}
		return sb.toString();
	}

	public static String getInfoAboutItem(Queue.Item item) {
		String time = null;
		if (Queue.WaitingItem.class.isInstance(item)) {
			Calendar timestamp = Queue.WaitingItem.class.cast(item).timestamp;
			Date date = timestamp.getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			time = dateFormat.format(date);
		}

		StringBuffer sb = new StringBuffer();
		sb.append(item.task.getDisplayName() + "(" + item.id + ") ");
		if (time != null) {
			sb.append(time + " - ");
		}
		for (RevisionParameterAction action : item
				.getActions(RevisionParameterAction.class)) {
			for (SvnInfo rev : action.getRevisions()) {
				sb.append(rev.revision + " ");
			}
			sb.append(" , ");
		}
		return sb.toString();
	}

	public static String getInfo() {
		Queue q = Jenkins.getInstance().getQueue();
		StringBuffer sb = new StringBuffer();
		sb.append("\n- WaitingList items: ");
		sb.append(getInfoAboutSet(q.getWaitingList()));
		sb.append("\n- BlockedProjects items: ");
		sb.append(getInfoAboutListBlocked(q.getBlockedProjects()));
		sb.append("\n- Buildables items: ");
		sb.append(getInfoAboutList(q.getBuildableItems()));
		sb.append("\n- Pending items: ");
		sb.append(getInfoAboutList(q.getPendingItems()));
		sb.append("\n- Parked items (with workUnit): ");
		sb.append(getInfoAboutParked(q.getParked()));

		return sb.toString();
	}

}
