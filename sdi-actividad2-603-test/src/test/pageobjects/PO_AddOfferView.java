package test.pageobjects;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import test.util.SeleniumUtils;

public class PO_AddOfferView  extends PO_NavView {
	
	static public void fillForm(WebDriver driver, String description, String details, String
			price) {
		WebElement descriptionw = driver.findElement(By.name("description"));
		descriptionw.click();
		descriptionw.clear();
		descriptionw.sendKeys(description);
		WebElement detailsw = driver.findElement(By.name("details"));
		detailsw.click();
		detailsw.clear();
		detailsw.sendKeys(details);
		WebElement pricew = driver.findElement(By.name("price"));
		pricew.click();
		pricew.clear();
		pricew.sendKeys(price);

		//Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}
