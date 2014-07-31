package florida.tony.hw6;

import java.util.List;
import florida.tony.hw6.UFOPosition;

/*
 * Code adapted from Scott Stanchfield
 */
interface RemoteServiceReporter {
	void report(in List<UFOPosition> ufoList);
}