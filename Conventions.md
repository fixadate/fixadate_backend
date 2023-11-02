<h1>Code Conventions</h1>

<h3>1. Naming</h3>

1-1. 식별자에는 영문/숫자/언더스코어만 허용 <br/>
변수명,  클래스명, 메서드 명에는 영어와 숫자만 허용 <br/>

1-2. 패키지 이름은 소문자로 구성 <br/>
cammel case나 언더스코어 사용을 권장하지 않는다 <br/>

1-3. 클래스/인터페이스 이름에는 대문자 카멜케이스 적용 <br/>
ex) <code>public class JwtRequestFilter</code> <br/>

1-4. 메서드 이름은 소문자 카멜케이스 적용 <br/>
ex) <code>public void saveAuthentication () {} </code> <br/>

1-5. 상수는 대문자와 언더스코어로 구성 <br/>
ex) <code>public final int JWT_ACCESSTOKEN_HEADER = "bearer ";</code> <br/>

1-6. 임시 변수 외에는 1 글자 변수명 사용 금지 <br/>
bad ex) <code>HtmlParser p = new HtmlParser();</code> <br/>

<h3>2. Declarations</h3>
2-1. import 시에 와일드카드 (*) 사용 자제 <br/>
bad ex) <code>import java.util.*;</code> <br/>

2-2. Long 형 값의 마지막에 'L'붙이기
