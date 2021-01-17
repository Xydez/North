package io.xydez.sandbox;

public class Main
{
	public static void main(String[] args)
	{
		SandboxApp app = new SandboxApp();

		try {
			app.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
