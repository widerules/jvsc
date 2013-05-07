package ca.jvsh.pulmonary;


public class QuizTab
{
	public int icon;
	public String	id;
	public String	name;

	public QuizTab(int icon,String id, String name)
	{
		this.icon = icon;
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}