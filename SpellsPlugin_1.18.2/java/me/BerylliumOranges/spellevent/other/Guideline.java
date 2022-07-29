package me.BerylliumOranges.spellevent.other;

import me.BerylliumOranges.spellevent.processes.SegmentProcess;

public class Guideline {
	SegmentProcess process;

//This class is meant to remind/force the correct implementation of new segments
	/**
	 * @param proc
	 * @param deleteTheProcess      If you are cloning the process, then you should
	 *                              delete the process.
	 * @param deleteOnFillSpellSlot do not delete on spell slot fill, generally
	 *                              false
	 */
	public Guideline(SegmentProcess proc, boolean deleteTheProcess, boolean deleteOnFillSpellSlot) {
		process = proc;
		if (deleteTheProcess) {
			proc.getSpellSlot().removeSegmentProcess(proc);
			proc = null;
		} else if (deleteOnFillSpellSlot)
			proc.setDeleteWhenSpellSlotIsNotEmpty(true /** deleteOnFillSpellSlot **/
			);
	}

	public SegmentProcess getProcess() {
		return process;
	}

	public void setProcess(SegmentProcess process) {
		this.process = process;
	}

}
