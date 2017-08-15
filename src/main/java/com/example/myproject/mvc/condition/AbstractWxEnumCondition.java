package com.example.myproject.mvc.condition;

import com.example.myproject.module.WxRequest;
import com.example.myproject.mvc.WxRequestResponseUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * A logical disjunction (' || ') request condition that matches a request
 * against a set of {@link RequestMethod}s.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractWxEnumCondition<T extends Enum<T>> extends AbstractRequestCondition<AbstractWxEnumCondition<T>> {

	protected final Set<T> enums;

	public AbstractWxEnumCondition(T... enums) {
		this(enums != null ? Arrays.asList(enums) : Collections.emptyList());
	}

	protected AbstractWxEnumCondition(Collection<T> enums) {
		this.enums = Collections.unmodifiableSet(new LinkedHashSet<>(enums));
	}

	public Set<T> getEnums() {
		return this.enums;
	}

	@Override
	protected Collection<T> getContent() {
		return this.enums;
	}

	@Override
	protected String getToStringInfix() {
		return " || ";
	}

	/**
	 * Returns a new instance with a union of the HTTP request enums
	 * from "this" and the "other" instance.
	 */
	@Override
	public abstract AbstractWxEnumCondition combine(AbstractWxEnumCondition other);
	/**
	 * Check if any of the HTTP request enums match the given request and
	 * return an instance that contains the matching HTTP request method only.
	 * @param request the current request
	 * @return the same instance if the condition is empty (unless the request
	 * method is HTTP OPTIONS), a new condition with the matched request method,
	 * or {@code null} if there is no match or the condition is empty and the
	 * request method is OPTIONS.
	 */
	@Override
	public AbstractWxEnumCondition getMatchingCondition(HttpServletRequest request) {
		return matchEnum(WxRequestResponseUtils.getWxRequestFromRequestAttribute(request));
	}

	protected abstract AbstractWxEnumCondition matchEnum(WxRequest wxRequest);

	/**
	 * Returns:
	 * <ul>
	 * <li>0 if the two conditions contain the same number of HTTP request enums
	 * <li>Less than 0 if "this" instance has an HTTP request method but "other" doesn't
	 * <li>Greater than 0 "other" has an HTTP request method but "this" doesn't
	 * </ul>
	 * <p>It is assumed that both instances have been obtained via
	 * {@link #getMatchingCondition(HttpServletRequest)} and therefore each instance
	 * contains the matching HTTP request method only or is otherwise empty.
	 */
	@Override
	public int compareTo(AbstractWxEnumCondition<T> other, HttpServletRequest request) {
		if (other.enums.size() != this.enums.size()) {
			return other.enums.size() - this.enums.size();
		}
		else if (this.enums.size() == 1) {
			// 以第一个元素算
			return this.enums.stream().findFirst().get().ordinal() - other.enums.stream().sorted().findFirst().map(e -> e.ordinal()).orElse(-1);
		}
		return 0;
	}

}
