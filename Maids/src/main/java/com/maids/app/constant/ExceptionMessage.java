package com.maids.app.constant;

public class ExceptionMessage {

    public interface NotFound {
        public static final String BOOK_ID = "Book id does not exist";
        public static final String PATRON_ID = "Patron id does not exist";

    }

    public interface Forbidden {
        public static final String BOOK_ALREADY_BORROWED = "Patron already borrowed this book";
        public static final String BOOK_NOT_BORROWED = "Patron did not borrow this book";
    }

    public interface Validation {
		public static final String REQUIRED_TITLE = "title is required";
		public static final String REQUIRED_AUTHOR = "author is required";
		public static final String REQUIRED_PUBLICATION_YEAR = "publicationYear is required";
		public static final String MIN_PUBLICATION_YEAR = "publicationYear should not be less than 1000";
		public static final String PUBLICATION_MAX_YEAR = "2024 is the latest publication year";
		public static final String REQUIRED_ISBN = "isbn is required";
		public static final String REQUIRED_NAME = "name is required";
		public static final String REQUIRED_CONTACT_INFORMATION = "contactInformation is required";

    }

}
