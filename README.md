# svc-forecast

Sample service that leverages Scala and Play! framework to deliver a weekly forecast interface via Open Weather.

## Requirements
- Installation of scala and sbt (sbt is a scala build tool required by Play!)
  (Easiest method: Install hombrew and `brew install scala sbt`)

## Running app
- Navigate to project directory and run `sbt run`
- In your browser navigate to `localhost:9000/forecast`

## Running unit tests
- Navigate to project directory and run `sbt test`


### Misc Notes
1. There is form validation for latitude (values between -90, 90) and longitude (values -180, 180) and blank/non-number values. Therefore I skipped backend validation on these for the sake of time.
2. Server errors are displayed on the form with a simple message for various scenarios (invalid api key, etc)
3. Unit testing focus was on the service layers, not controllers for sake of time.
4. I realize committing api keys directly to a repository is bad practice, more time saving.
