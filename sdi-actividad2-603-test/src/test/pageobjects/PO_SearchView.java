package test.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import test.util.SeleniumUtils;

public class PO_SearchView  extends PO_NavView {
	
	static public void fillForm(WebDriver driver, String search) {
		WebElement searchw = driver.findElement(By.name("searchText"));
		searchw.click();
		searchw.clear();
		searchw.sendKeys(search);

		//Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}
