package storybook.importer;

public class person{
	private String first;
	private String last;
	private String abrv;
	
	public void set_firstName(String s){
		first = s;
	}
	public String getFirst(){
		return first;
	}
	public void set_lastName(String s){
		last = s;
	}
	public String getLast(){
		return last;
	}
	public void set_abrv(String s){
		abrv = s;
	}
	public String getAbrv(){
		return abrv;
	}
}
