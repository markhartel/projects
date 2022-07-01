
function degreesToRadians(degrees) {
  return (degrees * Math.PI)/180;
}

function radiansToDegrees(radians) {
  return (radians * 180)/Math.PI;
}

// This algoritm is from the book "The World is Round" by Tony Rothman.
function SolarElevation(dayLength, yearLength, planetTilt, observerLatitude) {
	this.dayLength = dayLength;
	this.yearLength = yearLength;
	this.planetTilt = planetTilt;
	this.observerLatitude = observerLatitude;
	this.theta = degreesToRadians(this.observerLatitude);
	this.phi = degreesToRadians(this.planetTilt);
	this.alpha = 2.0*Math.PI/this.dayLength;
	this.beta = 2.0*Math.PI/this.yearLength;
	this.costheta = Math.cos(this.theta);
	this.sintheta = Math.sin(this.theta);
	this.cosphi = Math.cos(this.phi);
	this.sinphi = Math.sin(this.phi);
	this.calculate = function(time) {
		var alphaAngle = this.alpha * time;
		var betaAngle = this.beta * time;
		var coszeta = ((this.costheta*this.cosphi*Math.cos(alphaAngle) + this.sintheta*this.sinphi)*Math.cos(betaAngle)) + 
			(this.costheta*Math.sin(alphaAngle)*Math.sin(betaAngle));
		var zeta = Math.acos(coszeta);
		var elevation = -(90.0 - radiansToDegrees(zeta));
		return elevation;
	};
	this.calculateAz = function(time) {
		var alphaAngle = this.alpha * time;
		var betaAngle = this.beta * time;
		return radiansToDegrees(alphaAngle - betaAngle)  % 360;
//		var x = time/this.dayLength;
//		var azimuth = (x - Math.floor(x)) * 360.0; 
//		return azimuth;
	};
	this.position = function(time) {
		return [this.calculateAz(time), this.calculate(time)];
	};
}

