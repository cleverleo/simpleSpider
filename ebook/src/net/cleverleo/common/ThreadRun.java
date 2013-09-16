package net.cleverleo.common;

public abstract class ThreadRun implements Runnable {
	
	private String _name = null;

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		this.print("线程启动");
		T.lockInc();
		this.runing();
		T.lockDec();
		this.print("线程结束");
	}
	
	public void runing(){
		this.print("error");
	}
	
	protected void print(String msg){
		String name = null;
		if(this._name==null){
			name = Thread.currentThread().getName();
		}else{
			name = this._name;
		}
		System.out.println(String.format("%s: %s", name,msg));
	}
	
	public void setName(String name){
		this._name = name;
	}

}
