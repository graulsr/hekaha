package com.esy.jaha.osmdroid.spinner;


public class Categorias{

	private int id_categoria;

	private String name;
	
	private String icon;

	public Categorias(int id_categoria, String nombre, String icono)
	{
		this.id_categoria = id_categoria;
		this.name = nombre;
		this.icon = icono;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getIcon()
	{
		return icon;
	}

	public int getId_categoria()
	{
		return id_categoria;
	}

	public void setIcon(String icon)
	{
		this.icon = icon;
	}	

}