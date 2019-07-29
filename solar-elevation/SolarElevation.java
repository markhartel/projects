import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class SolarElevation 
{
	private String planetType;
	private double dayLength;
	private double yearLength;
	private double planetTilt;
	private double observerLatitude;
	private double phi;
	private double theta;
	private double alpha;
	private double beta;
	private double costheta;
	private double sintheta;
	private double cosphi;
	private double sinphi;
	
	public SolarElevation(String planetType, double dayLength, double yearLength, double planetTilt, double observerLatitude)
	{
		this.planetType = planetType;
		this.dayLength = dayLength;
		this.yearLength = yearLength;
		this.planetTilt = planetTilt;
		this.observerLatitude = observerLatitude;
		theta = Math.toRadians(observerLatitude);
		phi = Math.toRadians(planetTilt);
		alpha = 2.0*Math.PI/dayLength;
		beta = 2.0*Math.PI/yearLength;
		costheta = Math.cos(theta);
		sintheta = Math.sin(theta);
		cosphi = Math.cos(phi);
		sinphi = Math.sin(phi);
	}
	
	public String getPlanetType()
	{
		return planetType;
	}

	public double getDayLength()
	{
		return dayLength;
	}

	public double getYearLength()
	{
		return yearLength;
	}
	 
	public double getPlanetTilt()
	{
		return planetTilt;
	}
	
	public double getObserverLatitude()
	{
		return observerLatitude;
	}
	
	public double calculate(double time)
	{
		double alphaAngle = alpha * time;
		double betaAngle = beta * time;
		double coszeta = ((costheta*cosphi*Math.cos(alphaAngle) + sintheta*sinphi)*Math.cos(betaAngle)) + 
			(costheta*Math.sin(alphaAngle)*Math.sin(betaAngle));
		double zeta = Math.acos(coszeta);
		double elevation = 90.0 - Math.toDegrees(zeta);
		return elevation;
	}
	
	static public void main(String[] args) throws Exception
	{
		for (double lat = 0; lat <= 90; lat += 10)
		{
			SolarElevation solar;
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("StandardEarth"))
				{
					solar = getStandardEarth(lat);
				}
				else if (args[0].equalsIgnoreCase("TiltedEarth"))
				{
					solar = getTiltedEarth(lat);
				}
				else if (args[0].equalsIgnoreCase("ToppledEarth"))
				{
					solar = getToppledEarth(lat);
				}
				else if (args[0].equalsIgnoreCase("StandardVenus"))
				{
					solar = getStandardVenus(lat);
				}
				else
				{
					System.out.println("Error: unknown planet type " + args[0]);
					return;
				}
			}
			else
			{
				solar = getStandardEarth(lat);
			}
			runSimulation(solar);
		}
	}
	
	static public void runSimulation(SolarElevation solar) throws IOException
	{
		double maxElev = -999.0;
		double maxElevTime = 1;
		double minElev = 999.0;
		double minElevTime = -1;
		double oldElev = 999.0;
		int sunriseCount = 0;
		int sunsetCount = 0;

		File csvFile = new File("SolarElevation" + (int)solar.getObserverLatitude() + ".csv");
		PrintWriter writer = new PrintWriter(csvFile);
		writer.println("Time (hours),Time (days:hours),Elevation (degrees),Event");

		for (double time = 0.0; time <= solar.getYearLength(); time += 1.0)
		{
			double elevation = solar.calculate(time);
			if (elevation > maxElev)
			{
				maxElev = elevation;
				maxElevTime = time;
			}
			if (elevation < minElev)
			{
				minElev = elevation;
				minElevTime = time;
			}
			String event;
			if (elevation > 0)
			{
				event = "daytime";
			}
			else
			{
				event = "nighttime";
			}
			
			if (oldElev < 999.0)
			{
				if (oldElev <= 0.0 && elevation >= 0.0)
				{
					event = "sunrise";
					sunriseCount++;
				}
				else if (oldElev >= 0.0 && elevation <= 0.0)
				{
					event = "sunset";
					sunsetCount++;
				}
			}
			oldElev = elevation;
			writer.println(time + "," + formatTime(time, solar.getDayLength()) + "," + elevation + "," + event);
			//writer.println(elevation);
		}
		writer.close();
		System.out.println("planet type: " + solar.getPlanetType());
		System.out.println("observer latitude: " + solar.getObserverLatitude());
		System.out.println("maximum solar elevation " + maxElev + " degrees at time " + formatTime(maxElevTime, solar.getDayLength()));
		System.out.println("minimum solar elevation " + minElev + " degrees at time " + formatTime(minElevTime, solar.getDayLength()));
		System.out.println("number of sunrises: " + sunriseCount);
		System.out.println("number of sunsets: " + sunsetCount);		
	}
	
	static public String formatTime(double time, double dayLength)
	{
		int days = (int)(time/dayLength);
		int hours = (int)(time%dayLength);
		return "" + days + ":" + hours;
	}
	
	static public SolarElevation getStandardEarth(double observerLatitude)
	{
		double hoursPerDay = 24.0;
		double hoursPerYear = hoursPerDay*365.25;
		double planetTiltInDegrees = 23.5;
		return new SolarElevation("StandardEarth", hoursPerDay, hoursPerYear, planetTiltInDegrees, observerLatitude);
	}

	static public SolarElevation getStandardVenus(double observerLatitude)
	{
		double hoursPerDay = 24.0*243.0;
		double hoursPerYear = 24.0*225.0;
		double planetTiltInDegrees = 177.36;
		return new SolarElevation("StandardVenus", hoursPerDay, hoursPerYear, planetTiltInDegrees, observerLatitude);
	}

	static public SolarElevation getToppledEarth(double observerLatitude)
	{
		double hoursPerDay = 24.0;
		double hoursPerYear = hoursPerDay*365.25;
		double planetTiltInDegrees = 90.0;
		return new SolarElevation("ToppledEarth", hoursPerDay, hoursPerYear, planetTiltInDegrees, observerLatitude);
	}

	static public SolarElevation getTiltedEarth(double observerLatitude)
	{
		double hoursPerDay = 24.0;
		double hoursPerYear = hoursPerDay*365.25;
		double planetTiltInDegrees = 45.0;
		return new SolarElevation("TiltedEarth", hoursPerDay, hoursPerYear, planetTiltInDegrees, observerLatitude);
	}
	
//	static public SolarElevation getPatraBannk(double observerLatitude)
//	{
//		double hoursPerDay = TBD;
//		double hoursPerYear = TBD;
//		double planetTiltInDegrees = 20.0;
//		return new SolarElevation(hoursPerDay, hoursPerYear, planetTiltInDegrees, observerLatitude);
//	}

}
