Questions to resolve:

What is a record?
	Data objects -> only serve as input or output for other objects to operate on them
		- > usually immutable (declare final to make property unchangeable)

	A record is a simpler version of a Data Object:
		record PetRecord(int id, String name, String type) {}
		this initializes a pet record data object with a value for id, name, and type
		(which is all we need because data objects don't have methods)

	
	When you use Java records you get all of the following benefits.

		Immutability. All fields are final.
		Simplified constructor syntax.		
		Automatic getters.
		Automatic equals that compares all the fields.
		Automatic hashcode that calculates based on all the fields.
		Automatic toString that represents all the fields.
	
	I can put a method within a record, like if I want to rename a pet, but that just makes a new object since it's immutable


