package florida.tony.hw6;
import florida.tony.hw6.RemoteServiceReporter;

/*
 * Code adapted from Scott Stanchfield
 */
interface RemoteService {
	void reset();
	void add(RemoteServiceReporter reporter);
	void remove(RemoteServiceReporter reporter);
}
