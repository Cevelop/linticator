/*
 * The following method is missing a return statement.
 * */

#include <string>

std::string shouldReturn() {

	if(1 == 2) {

	} else {
		return std::string();
	}
}
