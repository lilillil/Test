package org.zerock.domain;

public class PageMaker {

	private int totalCount;
	private int startPage;
	private int endPage;
	private boolean prev;
	private boolean next;
	
	private int displayPageNum = 10;
	
	private Criteria cri;
	
	public void setCri(Criteria cri) {
		this.cri = cri;
	}
	
	public void setTotalCount(int totalCont) {
		this.totalCount = totalCount;
		calcData();
	}
	
	private void calcData() {
		endPage = (int)(Math.ceil(cri.getPage()/(double) displayPageNum) * displayPageNum );
		startPage = (endPage - displayPageNum) + 1;
	}
	
	
}
